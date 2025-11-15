package eu.nicosworld.stoptaclop.infrastructure.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import eu.nicosworld.stoptaclop.AbstractE2ETest;
import eu.nicosworld.stoptaclop.authentication.UserRepository;
import eu.nicosworld.stoptaclop.authentication.model.User;
import eu.nicosworld.stoptaclop.authentication.model.UserLoginDTO;
import eu.nicosworld.stoptaclop.authentication.model.UserRegistrationDTO;
import eu.nicosworld.stoptaclop.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.Smoked;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.SmokedRepository;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

class SmokedControllerE2ETest extends AbstractE2ETest {

  @Autowired AuthenticatedUserService authenticatedUserService;

  @Autowired UserRepository userRepository;

  @Autowired SmokedRepository smokedRepository;

  private String token;
  private String testEmail;
  private final String testPassword = "password123";

  /** Génère un email unique pour chaque test */
  private String generateRandomEmail() {
    return "test+" + System.currentTimeMillis() + "@example.com";
  }

  /** Inscription + login → retourne accessToken */
  private String getAccessToken(String email, String password) {
    // Inscription
    UserRegistrationDTO registrationDto = new UserRegistrationDTO();
    registrationDto.setEmail(email);
    registrationDto.setPassword(password);

    given()
        .contentType(ContentType.JSON)
        .body(registrationDto)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(201);

    // Login
    UserLoginDTO loginDto = new UserLoginDTO();
    loginDto.setEmail(email);
    loginDto.setPassword(password);

    return given()
        .contentType(ContentType.JSON)
        .body(loginDto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .path("accessToken");
  }

  /** Avant chaque test : vide la base et crée un utilisateur */
  @BeforeEach
  void setup() {
    smokedRepository.deleteAll();
    testEmail = generateRandomEmail();
    token = getAccessToken(testEmail, testPassword);
  }

  /** Retourne un RequestSpecification avec le token déjà appliqué */
  private RequestSpecification auth() {
    return given().header("Authorization", "Bearer " + token);
  }

  // -------------------------------------------------------
  //             Tests pour utilisateur authentifié
  // -------------------------------------------------------
  @Nested
  @DisplayName("Quand l'utilisateur est authentifié")
  class AuthenticatedTests {

    @Test
    @DisplayName("GET /smoked retourne les stats initiales")
    void getSmoked_initialStats() {
      auth()
          .when()
          .get("/smoked")
          .then()
          .statusCode(200)
          .body("smokedToday", equalTo(0))
          .body("totalSmoked", equalTo(0))
          .body("smokedLastWeek", hasSize(0));
    }

    @Test
    @DisplayName("POST /smoked enregistre une cigarette")
    void smokeOnce_updatesStats() {
      auth()
          .when()
          .post("/smoked")
          .then()
          .statusCode(200)
          .body("smokedToday", equalTo(1))
          .body("totalSmoked", equalTo(1))
          .body("smokedLastWeek", hasSize(1));
    }

    @Test
    @DisplayName("POST /smoked x3 incrémente correctement le total")
    void smokeThreeTimes() {
      User user = userRepository.findByEmail(testEmail).orElseThrow();
      AuthenticatedUser authenticatedUser = authenticatedUserService.findByUser(user);

      // Historique en base : 1 cigarette hier et 1 il y a 1h
      smokedRepository.save(new Smoked(authenticatedUser, LocalDateTime.now().minusHours(1)));
      smokedRepository.save(new Smoked(authenticatedUser, LocalDateTime.now().minusDays(1)));

      auth()
          .when()
          .post("/smoked")
          .then()
          .statusCode(200)
          .body("smokedToday", equalTo(2))
          .body("totalSmoked", equalTo(3))
          .body(
              "smokedLastWeek.day",
              containsInAnyOrder(
                  LocalDateTime.now().minusDays(1).toLocalDate().toString(),
                  LocalDateTime.now().toLocalDate().toString()));
    }

    @Test
    @DisplayName("POST /smoked limite à 1 cigarette par minute")
    void smokeTwiceWithinOneMinute_returns429() {
      auth().when().post("/smoked").then().statusCode(200);

      auth()
          .when()
          .post("/smoked")
          .then()
          .statusCode(429)
          .body("error", equalTo("Vous ne pouvez pas fumer plus d'une fois par minute"));
    }
  }

  // -------------------------------------------------------
  //             Tests pour utilisateur non authentifié
  // -------------------------------------------------------
  @Nested
  @DisplayName("Quand l'utilisateur n'est pas authentifié")
  class UnauthorizedTests {

    @Test
    @DisplayName("GET /smoked retourne 401")
    void getSmoked_unauthorized() {
      given().when().get("/smoked").then().statusCode(401);
    }

    @Test
    @DisplayName("POST /smoked retourne 401")
    void postSmoked_unauthorized() {
      given().when().post("/smoked").then().statusCode(401);
    }
  }
}
