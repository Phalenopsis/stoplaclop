package eu.nicosworld.stoptaclop.domain.saving;

import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;

public class SavingToday {
  private final int cigarettesSmokedToday;
  private final int cigarettePerDay;

  public SavingToday(int cigarettesSmokedToday, AuthenticatedUser user) {
    this.cigarettesSmokedToday = cigarettesSmokedToday;
    this.cigarettePerDay = user.getInitialSmokedByDay();
  }

  public double getSavedMoneyToday() {
    return (cigarettePerDay - cigarettesSmokedToday)
        * Saving.PACK_OF_CIGARETTE_PRICE
        / Saving.NUMBER_OF_CIGARETTE_IN_PACK;
  }
}
