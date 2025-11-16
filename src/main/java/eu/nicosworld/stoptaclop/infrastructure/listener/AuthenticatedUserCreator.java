package eu.nicosworld.stoptaclop.infrastructure.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import eu.nicosworld.stoptaclop.authentication.model.User;
import eu.nicosworld.stoptaclop.infrastructure.event.UserCreatedEvent;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.AuthenticatedUserRepository;

@Component
public class AuthenticatedUserCreator {

  private final AuthenticatedUserRepository authenticatedUserRepository;

  public AuthenticatedUserCreator(AuthenticatedUserRepository authenticatedUserRepository) {
    this.authenticatedUserRepository = authenticatedUserRepository;
  }

  @EventListener
  public void handleUserCreated(UserCreatedEvent event) {
    User user = event.getUser();
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
    authenticatedUserRepository.save(authenticatedUser);
  }
}
