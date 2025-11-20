package eu.nicosworld.stoptaclop.infrastructure.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
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
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokingStatsDto;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

class SmokedControllerE2ETest extends AbstractE2ETest {

  @Autowired AuthenticatedUserService authenticatedUserService;

  @Autowired UserRepository userRepository;

  @Autowired SmokedRepository smokedRepository;

  private String token;
  private String testEmail;
  private final String testPassword = "password123";

  private String generateRandomEmail() {
    return "test+" + System.currentTimeMillis() + "@example.com";
  }

  private String getAccessToken(String email, String password) {
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

  @BeforeEach
  void setup() {
    smokedRepository.deleteAll();
    testEmail = generateRandomEmail();
    token = getAccessToken(testEmail, testPassword);
  }

  private RequestSpecification auth() {
    return given().header("Authorization", "Bearer " + token);
  }

  @Nested
  @DisplayName("Quand l'utilisateur est authentifié")
  class AuthenticatedTests {

    @Test
    @DisplayName("GET /smoked retourne les stats initiales")
    void getSmoked_initialStats() {
      UserSmokingStatsDto dto =
          auth()
              .when()
              .get("/smoked")
              .then()
              .statusCode(200)
              .extract()
              .as(UserSmokingStatsDto.class);

      Assertions.assertEquals(0, dto.stats().smokedToday());
      Assertions.assertTrue(dto.stats().smokedLastWeek().isEmpty());
      Assertions.assertEquals(12.5, dto.financial().savedMoney());
      Assertions.assertEquals(0.0, dto.financial().burnedMoney());
      Assertions.assertNull(dto.stats().minutesSinceLastSmoked());
    }

    @Test
    @DisplayName("POST /smoked enregistre une cigarette")
    void smokeOnce_updatesStats() {
      auth().when().post("/smoked").then().statusCode(200);

      UserSmokingStatsDto dto =
          auth()
              .when()
              .get("/smoked")
              .then()
              .statusCode(200)
              .extract()
              .as(UserSmokingStatsDto.class);

      Assertions.assertEquals(1, dto.stats().smokedToday());
      Assertions.assertEquals(1, dto.stats().smokedLastWeek().size());
      Assertions.assertTrue(dto.financial().savedMoney() >= 0);
      Assertions.assertTrue(dto.financial().burnedMoney() >= 0);
      Assertions.assertTrue(dto.stats().minutesSinceLastSmoked() >= 0);
    }

    @Test
    @DisplayName("POST /smoked x3 incrémente correctement le total et calcule bien les stats")
    void smokeThreeTimes_robust() {
      User user = userRepository.findByEmail(testEmail).orElseThrow();
      AuthenticatedUser authenticatedUser = authenticatedUserService.findByUser(user);

      // Historique : 1 cigarette hier, 1 il y a 2 heures
      smokedRepository.save(new Smoked(authenticatedUser, LocalDateTime.now().minusDays(1)));
      smokedRepository.save(new Smoked(authenticatedUser, LocalDateTime.now().minusHours(2)));

      // Smoke aujourd'hui via POST
      auth().when().post("/smoked").then().statusCode(200);

      UserSmokingStatsDto dto =
          auth()
              .when()
              .get("/smoked")
              .then()
              .statusCode(200)
              .extract()
              .as(UserSmokingStatsDto.class);

      // smokedToday = 2 (1 cigarette il y a 2h + 1 maintenant)
      Assertions.assertEquals(2, dto.stats().smokedToday());

      // smokedLastWeek contient exactly 2 jours : yesterday et today
      Assertions.assertEquals(2, dto.stats().smokedLastWeek().size());
      Assertions.assertTrue(
          dto.stats().smokedLastWeek().stream()
              .anyMatch(s -> s.getDay().equals(LocalDate.now()) && s.getCount() == 2));
      Assertions.assertTrue(
          dto.stats().smokedLastWeek().stream()
              .anyMatch(s -> s.getDay().equals(LocalDate.now().minusDays(1)) && s.getCount() == 1));

      // minutesSinceLastSmoked corresponds à la cigarette la plus récente
      long minutesSinceLast = dto.stats().minutesSinceLastSmoked();
      Assertions.assertTrue(minutesSinceLast >= 0 && minutesSinceLast < 2);

      // Vérifier que les stats financières sont positives
      Assertions.assertTrue(dto.financial().savedMoney() >= 0);
      Assertions.assertTrue(dto.financial().burnedMoney() >= 0);
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
