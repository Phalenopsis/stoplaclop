package eu.nicosworld.stoptaclop.domain.saving;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import eu.nicosworld.stoptaclop.authentication.model.User;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;

class SavingTest {

  private AuthenticatedUser createUser(int initialSmokedByDay) {
    User user = new User();
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
    authenticatedUser.setInitialSmokedByDay(initialSmokedByDay);
    return authenticatedUser;
  }

  @Nested
  @DisplayName("Tests for getSavedMoney()")
  class GetSavedMoneyTests {

    @Test
    @DisplayName("Positive when user smokes less than expected")
    void testSavedMoney_whenUserSmokesLessThanBefore() {
      Saving saving = new Saving(50L, 10L);
      AuthenticatedUser user = createUser(10);
      double expected = (10 * 11 - 50) * 12.5 / 20;
      assertEquals(expected, saving.getSavedMoney(user), 0.0001);
    }

    @Test
    @DisplayName("Zero when user smokes exactly expected amount")
    void testSavedMoney_whenUserSmokesExactlySameAmount() {
      Saving saving = new Saving(110L, 10L);
      AuthenticatedUser user = createUser(10);
      assertEquals(0.0, saving.getSavedMoney(user), 0.0001);
    }

    @Test
    @DisplayName("Day 1 with 0 days recorded")
    void testSavedMoney_firstDay() {
      Saving saving = new Saving(10L, 0L);
      AuthenticatedUser user = createUser(20);
      double expected = (20 * 1 - 10) * 12.5 / 20;
      assertEquals(expected, saving.getSavedMoney(user), 0.0001);
    }

    @Test
    @DisplayName("During the current day")
    void testSavedMoney_currentDayBehavior() {
      Saving saving = new Saving(30L, 1L);
      AuthenticatedUser user = createUser(20);
      double expected = 6.25;
      assertEquals(expected, saving.getSavedMoney(user), 0.0001);
    }

    @Test
    @DisplayName("Zero days but some cigarettes smoked")
    void testSavedMoney_zeroDaysButSomeCigarettesSmoked() {
      Saving saving = new Saving(4L, 0L);
      AuthenticatedUser user = createUser(20);
      double expected = (20 * 1 - 4) * 12.5 / 20;
      assertEquals(expected, saving.getSavedMoney(user), 0.0001);
    }
  }

  @Nested
  @DisplayName("Tests for getBurnedMoney()")
  class GetBurnedMoneyTests {

    @Test
    @DisplayName("Zero cigarettes smoked")
    void testBurnedMoney_zeroCigarettes() {
      Saving saving = new Saving(0L, 5L);
      assertEquals(0.0, saving.getBurnedMoney(), 0.0001);
    }

    @Test
    @DisplayName("Ten cigarettes smoked")
    void testBurnedMoney_tenCigarettes() {
      Saving saving = new Saving(10L, 1L);
      double expected = 10 * 12.5 / 20;
      assertEquals(expected, saving.getBurnedMoney(), 0.0001);
    }

    @Test
    @DisplayName("Fifty cigarettes smoked")
    void testBurnedMoney_fiftyCigarettes() {
      Saving saving = new Saving(50L, 10L);
      double expected = 50 * 12.5 / 20;
      assertEquals(expected, saving.getBurnedMoney(), 0.0001);
    }
  }
}
