package eu.nicosworld.stoptaclop.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Smoked {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AuthenticatedUser user;

  private LocalDateTime date;

  public Smoked() { }

  public Smoked(AuthenticatedUser user, LocalDateTime date) {
    this.user = user;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AuthenticatedUser getUser() {
    return user;
  }

  public void setUser(AuthenticatedUser user) {
    this.user = user;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }
}
