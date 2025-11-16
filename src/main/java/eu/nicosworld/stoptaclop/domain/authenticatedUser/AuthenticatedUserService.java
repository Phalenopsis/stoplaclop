package eu.nicosworld.stoptaclop.domain.authenticatedUser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import eu.nicosworld.stoptaclop.authentication.UserRepository;
import eu.nicosworld.stoptaclop.authentication.model.User;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.AuthenticatedUserRepository;

@Service
public class AuthenticatedUserService {
  private final AuthenticatedUserRepository authenticatedUserRepository;
  private final UserRepository userRepository;

  public AuthenticatedUserService(
      AuthenticatedUserRepository authenticatedUserRepository, UserRepository userRepository) {
    this.authenticatedUserRepository = authenticatedUserRepository;
    this.userRepository = userRepository;
  }

  public AuthenticatedUser findByUser(UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    return authenticatedUserRepository.findById(user.getId()).orElseThrow();
  }
}
