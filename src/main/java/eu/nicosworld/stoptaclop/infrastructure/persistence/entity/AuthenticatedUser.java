package eu.nicosworld.stoptaclop.infrastructure.persistence.entity;

import java.util.List;

import eu.nicosworld.stoptaclop.authentication.model.User;
import jakarta.persistence.*;

@Entity
public class AuthenticatedUser {

  @Id private Long id;

  @OneToOne @MapsId private User user;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Smoked> smokedList;

  private int initialSmokedByDay = 20;

  public AuthenticatedUser() {}

  public AuthenticatedUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Smoked> getSmokedList() {
    return smokedList;
  }

  public void setSmokedList(List<Smoked> smokedList) {
    this.smokedList = smokedList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getInitialSmokedByDay() {
    return initialSmokedByDay;
  }

  public void setInitialSmokedByDay(int initialSmokedByDay) {
    this.initialSmokedByDay = initialSmokedByDay;
  }
}
