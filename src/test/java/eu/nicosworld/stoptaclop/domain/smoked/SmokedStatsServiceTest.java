package eu.nicosworld.stoptaclop.domain.smoked;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import eu.nicosworld.stoptaclop.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.Smoked;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.SmokedRepository;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokedDto;

@ExtendWith(MockitoExtension.class)
class SmokedStatsServiceTest {

  @Mock private SmokedRepository smokedRepository;

  @Mock private AuthenticatedUserService authenticatedUserService;

  @Mock private UserDetails userDetails;

  private SmokedStatsService service;

  @BeforeEach
  void setup() {
    service = new SmokedStatsService(smokedRepository, authenticatedUserService);
  }

  @Test
  void testGetUserSmokedStats() {
    // --- GIVEN ---
    AuthenticatedUser user = new AuthenticatedUser();

    // Simule une liste d'évènements Smoked dans l'entité User
    user.setSmokedList(
        List.of(smokedAt("2024-01-10"), smokedAt("2024-01-11"), smokedAt("2024-01-12")));

    when(authenticatedUserService.findByUser(userDetails)).thenReturn(user);

    LocalDate today = LocalDate.now();

    // Stats pour la semaine (utilisées par le repo)
    List<SmokedCountByDay> lastWeekStats =
        List.of(
            new SmokedCountByDay(today.minusDays(1), 3L),
            new SmokedCountByDay(today, 5L) // smokedToday = 5
            );

    when(smokedRepository.countSmokedByDay(eq(user), any(LocalDateTime.class)))
        .thenReturn(lastWeekStats);

    // Last smoked date for new field
    LocalDateTime lastSmoked = LocalDateTime.now().minusHours(3);
    when(smokedRepository.findLastSmokedDate(user)).thenReturn(lastSmoked);

    // --- WHEN ---
    UserSmokedDto dto = service.getUserSmokedStats(userDetails);

    // --- THEN ---
    assertThat(dto.smokedToday()).isEqualTo(5);
    assertThat(dto.smokedLastWeek()).containsExactlyElementsOf(lastWeekStats);
    assertThat(dto.totalSmoked()).isEqualTo(3);
    assertThat(dto.firstSmokedRecorded()).isEqualTo(LocalDate.parse("2024-01-10"));
    assertThat(dto.lastSmokedRecorded()).isEqualTo(lastSmoked);
  }

  // Utilitaire pour créer un Smoked avec une date simple
  private static Smoked smokedAt(String date) {
    Smoked s = new Smoked();
    s.setDate(LocalDate.parse(date).atStartOfDay());
    return s;
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("smokedTodayProvider")
  void testGetSmokedToday(String description, List<SmokedCountByDay> input, int expected) {
    int result = SmokedStatsService.getSmokedToday(input);
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> smokedTodayProvider() {
    LocalDate today = LocalDate.now();

    return Stream.of(
        Arguments.of(
            "Returns smoked count for today when there is one entry per day",
            List.of(new SmokedCountByDay(today, 3L), new SmokedCountByDay(today.minusDays(1), 2L)),
            3),
        Arguments.of(
            "Sums multiple entries for today",
            List.of(new SmokedCountByDay(today, 5L), new SmokedCountByDay(today, 2L)),
            7),
        Arguments.of(
            "No entry for today returns 0",
            List.of(new SmokedCountByDay(today.minusDays(2), 4L)),
            0),
        Arguments.of("Empty list returns 0", List.of(), 0));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("firstSmokedProvider")
  void testGetFirstSmokedRecorded(String description, List<String> dates, LocalDate expected) {
    AuthenticatedUser user = new AuthenticatedUser();
    user.setSmokedList(dates.stream().map(SmokedStatsServiceTest::smokedAt).toList());

    LocalDate result = SmokedStatsService.getFirstSmokedRecorded(user);

    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> firstSmokedProvider() {
    return Stream.of(
        Arguments.of(
            "Returns earliest date among several entries",
            List.of("2024-01-10", "2024-01-12", "2024-01-11"),
            LocalDate.parse("2024-01-10")),
        Arguments.of(
            "Single entry returns that date", List.of("2024-02-01"), LocalDate.parse("2024-02-01")),
        Arguments.of("Empty list returns null", List.of(), null));
  }
}
