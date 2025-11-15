package eu.nicosworld.stoptaclop.infrastructure.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.*;

import eu.nicosworld.stoptaclop.AbstractE2ETest;
import eu.nicosworld.stoptaclop.authentication.model.UserLoginDTO;
import eu.nicosworld.stoptaclop.authentication.model.UserRegistrationDTO;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

class SmokedControllerE2ETest extends AbstractE2ETest {

  /** Inscription + login → retourne accessToken */
  private String getAccessToken(String email, String password) {
    // register
    given()
        .contentType(ContentType.JSON)
        .body(
            new UserRegistrationDTO() {
              {
                setEmail(email);
                setPassword(password);
              }
            })
        .when()
        .post("/auth/register")
        .then()
        .statusCode(201);

    // login
    UserLoginDTO loginDto = new UserLoginDTO();
    loginDto.setEmail(email);
    loginDto.setPassword(password);

    Response loginResponse =
        given()
            .contentType(ContentType.JSON)
            .body(loginDto)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

    return loginResponse.jsonPath().getString("accessToken");
  }

  // -------------------------------------------------------
  //                  TESTS
  // -------------------------------------------------------

  @Test
  @DisplayName("GET /smoked - retourne les stats initiales d'un utilisateur")
  void getSmoked_initialStats() {
    String email = "testsmokegetinitialstats@example.com";
    String password = "password123";

    String token = getAccessToken(email, password);

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/smoked")
        .then()
        .statusCode(200)
        .body("smokedToday", equalTo(0))
        .body("totalSmoked", equalTo(0))
        .body("smokedLastWeek", hasSize(0));
  }

  @Test
  @DisplayName("POST /smoked - enregistre une cigarette et met à jour les stats")
  void smokeOnce_updatesStats() {
    String email = "testsmokepostone@example.com";
    String password = "password123";

    String token = getAccessToken(email, password);

    // Smoker une cigarette
    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .post("/smoked")
        .then()
        .statusCode(200)
        .body("smokedToday", equalTo(1))
        .body("totalSmoked", equalTo(1))
        .body("smokedLastWeek", hasSize(1));
  }

  @Test
  @DisplayName("POST /smoked x3 - incrémente correctement le total et le total du jour")
  void smokeThreeTimes() {
    String email = "testsmokepostthree@example.com";
    String password = "password123";

    String token = getAccessToken(email, password);

    for (int i = 0; i < 3; i++) {
      given()
          .header("Authorization", "Bearer " + token)
          .when()
          .post("/smoked")
          .then()
          .statusCode(200);
    }

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/smoked")
        .then()
        .statusCode(200)
        .body("smokedToday", equalTo(3))
        .body("totalSmoked", equalTo(3))
        .body("smokedLastWeek", hasSize(1)); // 1 entrée pour aujourd'hui
  }

  @Test
  @DisplayName("GET /smoked - doit renvoyer 401 si non authentifié")
  void getSmoked_unauthorized() {
    given().when().get("/smoked").then().statusCode(401);
  }

  @Test
  @DisplayName("POST /smoked - doit renvoyer 401 si non authentifié")
  void postSmoked_unauthorized() {
    given().when().post("/smoked").then().statusCode(401);
  }
}
