package eu.nicosworld.stoptaclop.infrastructure.event;

import org.springframework.context.ApplicationEvent;

import eu.nicosworld.stoptaclop.authentication.model.User;

public class UserCreatedEvent extends ApplicationEvent {

  private final User user;

  public UserCreatedEvent(Object source, User user) {
    super(source);
    this.user = user;
  }

  public User getUser() {
    return user;
  }
}
