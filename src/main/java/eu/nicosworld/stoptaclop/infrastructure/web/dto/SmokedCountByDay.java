package eu.nicosworld.stoptaclop.infrastructure.web.dto;

import java.time.LocalDate;

public class SmokedCountByDay {

  private LocalDate day;
  private Long count;

  // Constructeur utilisé par la requête JPQL
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

  // Setter si nécessaire
  public void setDay(LocalDate day) {
    this.day = day;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
