package eu.nicosworld.stoptaclop.infrastructure.web.dto;

import java.time.LocalDate;

/** Represents the number of cigarettes smoked on a given day. */
public class SmokedCountByDay {

  private LocalDate day;
  private Long count;

  /**
   * Represents the number of cigarettes smoked on a given day.
   *
   * <p>
   *
   * @param day the date (as a LocalDate) for which the count is computed
   * @param count the total number of cigarettes smoked on that date
   */
  public SmokedCountByDay(LocalDate day, Long count) {
    this.day = day;
    this.count = count;
  }

  // Getters
  public LocalDate getDay() {
    return day;
  }

  public Long getCount() {
    return count;
  }
}
