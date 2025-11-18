package eu.nicosworld.stoptaclop.domain.smoked;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.nicosworld.stoptaclop.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.SmokedRepository;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokedDto;

@Service
@Transactional(readOnly = true)
public class SmokedStatsService {

  private final SmokedRepository smokedRepository;
  private final AuthenticatedUserService authenticatedUserService;

  public SmokedStatsService(
      SmokedRepository smokedRepository, AuthenticatedUserService authenticatedUserService) {
    this.smokedRepository = smokedRepository;
    this.authenticatedUserService = authenticatedUserService;
  }

  public UserSmokedDto getUserSmokedStats(UserDetails userDetails) {
    // Récupérer l'AuthenticatedUser
    AuthenticatedUser user = authenticatedUserService.findByUser(userDetails);
    LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(6);
    List<SmokedCountByDay> smokedLastWeek = smokedRepository.countSmokedByDay(user, oneWeekAgo);

    int smokedToday = getSmokedToday(smokedLastWeek);

    LocalDate firstSmokedRecorded = getFirstSmokedRecorded(user);

    int totalSmoked = user.getSmokedList().size();

    LocalDateTime lastCigaretteSmoked = smokedRepository.findLastSmokedDate(user);

    return new UserSmokedDto(
        smokedToday,
        smokedLastWeek,
        firstSmokedRecorded,
        totalSmoked,
        lastCigaretteSmoked,
        LocalDateTime.now());
  }

  static LocalDate getFirstSmokedRecorded(AuthenticatedUser user) {
    return user.getSmokedList().stream()
        .map(smoked -> smoked.getDate().toLocalDate())
        .min(LocalDate::compareTo)
        .orElse(null);
  }

  static int getSmokedToday(List<SmokedCountByDay> smokedLastWeek) {
    return (int)
        smokedLastWeek.stream()
            .filter(s -> s.getDay().isEqual(LocalDate.now()))
            .mapToLong(SmokedCountByDay::getCount)
            .sum();
  }
}
