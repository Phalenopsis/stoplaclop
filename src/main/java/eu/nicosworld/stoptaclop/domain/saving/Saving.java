package eu.nicosworld.stoptaclop.domain.saving;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;

public class Saving {
  public static final double PACK_OF_CIGARETTE_PRICE = 12.5;
  public static final int NUMBER_OF_CIGARETTE_IN_PACK = 20;

  private final Long cigaretteSmoked;
  private final Long days;

  public Saving(Long cigaretteSmoked, LocalDate firstSmokedRecorded) {
    this(cigaretteSmoked, ChronoUnit.DAYS.between(firstSmokedRecorded, LocalDate.now()));
  }

  public Saving(Long cigaretteSmoked, Long days) {
    this.days = days;
    this.cigaretteSmoked = cigaretteSmoked;
  }

  /**
   * Calculates the money saved by the user based on their cigarette consumption.
   *
   * <p>The calculation considers the theoretical consumption up to the current day (including the
   * ongoing day) to provide an encouraging, positive feedback: even if the user hasn't finished the
   * day, the method estimates the money they would save if they stopped at that moment.
   *
   * @param user the authenticated user whose initial daily cigarette consumption is used
   * @return the estimated amount of money saved in euros
   */
  public double getSavedMoney(AuthenticatedUser user) {
    long effectiveDays = days + 1;
    long theoreticalConsumption = user.getInitialSmokedByDay() * effectiveDays;
    return (theoreticalConsumption - cigaretteSmoked)
        * PACK_OF_CIGARETTE_PRICE
        / NUMBER_OF_CIGARETTE_IN_PACK;
  }

  /**
   * Calculates the total money spent on cigarettes actually smoked by the user.
   *
   * @return the amount of money spent in euros
   */
  public double getBurnedMoney() {
    return cigaretteSmoked * PACK_OF_CIGARETTE_PRICE / NUMBER_OF_CIGARETTE_IN_PACK;
  }
}
