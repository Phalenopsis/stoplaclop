package eu.nicosworld.stoptaclop.domain.smoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import eu.nicosworld.stoptaclop.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.SmokedRepository;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokingStatsDto;

class SmokedStatsServiceTest {

  @Mock private SmokedRepository smokedRepository;

  @Mock private AuthenticatedUserService authenticatedUserService;

  @InjectMocks private SmokedStatsService smokedStatsService;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

  private AuthenticatedUser mockUser() {
    return new AuthenticatedUser();
  }

  private UserDetails mockUserDetails() {
    return mock(UserDetails.class);
  }

  @Nested
  @DisplayName("Financial stats tests")
  class FinancialStats {

    @Test
    @DisplayName("Saved and burned money calculation")
    void testSavedAndBurnedMoney() {
      AuthenticatedUser user = mockUser();
      UserDetails userDetails = mockUserDetails();
      when(authenticatedUserService.findByUser(userDetails)).thenReturn(user);

      LocalDateTime now = LocalDateTime.now();
      LocalDate firstSmoked = LocalDate.now().minusDays(4);

      when(smokedRepository.countTotalSmokedByUser(user)).thenReturn(60L);
      when(smokedRepository.findFirstSmokedDate(user)).thenReturn(firstSmoked.atStartOfDay());
      when(smokedRepository.countSmokedByDay(eq(user), any())).thenReturn(List.of());
      when(smokedRepository.findLastSmokedDate(user)).thenReturn(now.minusMinutes(30));

      UserSmokingStatsDto dto = smokedStatsService.getUserSmokingStats(userDetails);

      double savedMoney = dto.financial().savedMoney();
      double burnedMoney = dto.financial().burnedMoney();

      assert (savedMoney >= 0);
      assert (burnedMoney >= 0);
    }
  }

  @Nested
  @DisplayName("Behavioral stats tests")
  class BehavioralStats {

    @Test
    @DisplayName("Minutes since last smoked")
    void testMinutesSinceLastSmoked() {
      AuthenticatedUser user = mockUser();
      UserDetails userDetails = mockUserDetails();
      when(authenticatedUserService.findByUser(userDetails)).thenReturn(user);

      LocalDateTime now = LocalDateTime.now();
      LocalDate firstSmoked = LocalDate.now().minusDays(4);

      when(smokedRepository.countTotalSmokedByUser(user)).thenReturn(40L);
      when(smokedRepository.findFirstSmokedDate(user)).thenReturn(firstSmoked.atStartOfDay());
      when(smokedRepository.findLastSmokedDate(user)).thenReturn(now.minusMinutes(90));

      when(smokedRepository.countSmokedByDay(eq(user), any()))
          .thenReturn(
              List.of(
                  new SmokedCountByDay(LocalDate.now(), 5L),
                  new SmokedCountByDay(LocalDate.now().minusDays(1), 10L)));

      UserSmokingStatsDto dto = smokedStatsService.getUserSmokingStats(userDetails);

      assertEquals(5, dto.stats().smokedToday());
      assert (dto.stats().minutesSinceLastSmoked() >= 89
          && dto.stats().minutesSinceLastSmoked() <= 91);
    }

    @Test
    @DisplayName("Smoked last week mapping")
    void testSmokedLastWeekMapping() {
      AuthenticatedUser user = mockUser();
      UserDetails userDetails = mockUserDetails();
      when(authenticatedUserService.findByUser(userDetails)).thenReturn(user);

      LocalDateTime now = LocalDateTime.now();
      LocalDate firstSmoked = LocalDate.now().minusDays(6);

      when(smokedRepository.countTotalSmokedByUser(user)).thenReturn(50L);
      when(smokedRepository.findFirstSmokedDate(user)).thenReturn(firstSmoked.atStartOfDay());
      when(smokedRepository.findLastSmokedDate(user)).thenReturn(now.minusMinutes(45));

      when(smokedRepository.countSmokedByDay(eq(user), any()))
          .thenReturn(
              List.of(
                  new SmokedCountByDay(LocalDate.now(), 2L),
                  new SmokedCountByDay(LocalDate.now().minusDays(1), 3L),
                  new SmokedCountByDay(LocalDate.now().minusDays(2), 4L)));

      UserSmokingStatsDto dto = smokedStatsService.getUserSmokingStats(userDetails);

      List<SmokedCountByDay> week = dto.stats().smokedLastWeek();

      SmokedCountByDay today =
          week.stream().filter(s -> s.getDay().isEqual(LocalDate.now())).findFirst().orElseThrow();
      SmokedCountByDay yesterday =
          week.stream()
              .filter(s -> s.getDay().isEqual(LocalDate.now().minusDays(1)))
              .findFirst()
              .orElseThrow();
      SmokedCountByDay twoDaysAgo =
          week.stream()
              .filter(s -> s.getDay().isEqual(LocalDate.now().minusDays(2)))
              .findFirst()
              .orElseThrow();

      assertEquals(2L, today.getCount());
      assertEquals(3L, yesterday.getCount());
      assertEquals(4L, twoDaysAgo.getCount());
    }
  }
}
