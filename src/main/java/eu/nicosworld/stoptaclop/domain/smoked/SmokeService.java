package eu.nicosworld.stoptaclop.domain.smoked;

import java.time.Duration;
import java.time.LocalDateTime;

import eu.nicosworld.stoptaclop.exception.TooManySmokesException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import eu.nicosworld.stoptaclop.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.Smoked;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.SmokedRepository;

@Service
public class SmokeService {
  private final AuthenticatedUserService authenticatedUserService;
  private final SmokedRepository smokeRepository;

  public SmokeService(
      AuthenticatedUserService authenticatedUserService, SmokedRepository smokeRepository) {
    this.authenticatedUserService = authenticatedUserService;
    this.smokeRepository = smokeRepository;
  }

  public void smokeACigarette(UserDetails user) {
    AuthenticatedUser authenticatedUser = authenticatedUserService.findByUser(user);

    LocalDateTime lastSmokeDate = smokeRepository
            .findTopByUserOrderByDateDesc(authenticatedUser)
            .map(Smoked::getDate)
            .orElse(null);

    if (lastSmokeDate != null) {
      Duration sinceLastSmoke = Duration.between(lastSmokeDate, LocalDateTime.now());
      if (sinceLastSmoke.toSeconds() < 60) {
        throw new TooManySmokesException();
      }
    }

    Smoked smoked = new Smoked();
    smoked.setUser(authenticatedUser);
    smoked.setDate(LocalDateTime.now());
    smokeRepository.save(smoked);
  }
}
