package eu.nicosworld.stoptaclop.domain.smoked;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import eu.nicosworld.stoptaclop.domain.saving.Saving;
import eu.nicosworld.stoptaclop.domain.saving.SavingToday;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.FinancialStatsDto;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokingStatsDto;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokingStatsDto;

public class UserSmokingStatsMapper {

  public static UserSmokingStatsDto mapToDto(
      Saving saving,
      AuthenticatedUser user,
      List<SmokedCountByDay> smokedLastWeek,
      LocalDateTime lastSmokedDate) {

    int smokedToday =
        smokedLastWeek.stream()
            .filter(s -> s.getDay().isEqual(LocalDate.now()))
            .mapToInt(s -> s.getCount().intValue())
            .sum();

    FinancialStatsDto financial = getFinancialStats(saving, user, smokedToday);

    SmokingStatsDto stats = getSmokingStats(smokedLastWeek, lastSmokedDate, smokedToday);

    return new UserSmokingStatsDto(financial, stats);
  }

  private static SmokingStatsDto getSmokingStats(
      List<SmokedCountByDay> smokedLastWeek, LocalDateTime lastSmokedDate, int smokedToday) {
    LocalDateTime now = LocalDateTime.now();

    Long minutesSinceLastSmoked =
        lastSmokedDate != null ? Duration.between(lastSmokedDate, now).toMinutes() : null;

    return new SmokingStatsDto(smokedToday, smokedLastWeek, minutesSinceLastSmoked);
  }

  private static FinancialStatsDto getFinancialStats(
      Saving saving, AuthenticatedUser user, int smokedToday) {
    SavingToday savingToday = new SavingToday(smokedToday, user);

    return new FinancialStatsDto(
        saving.getBurnedMoney(), saving.getSavedMoney(user), savingToday.getSavedMoneyToday());
  }
}
