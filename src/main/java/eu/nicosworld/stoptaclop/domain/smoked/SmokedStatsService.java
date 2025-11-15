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
    AuthenticatedUser authUser = authenticatedUserService.findByUser(userDetails);

    // Récupérer les stats pour la dernière semaine
    LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(6); // incluant aujourd'hui
    List<SmokedCountByDay> smokedLastWeek = smokedRepository.countSmokedByDay(authUser, oneWeekAgo);

    // Calculer smokedToday
    int smokedToday =
        (int)
            smokedLastWeek.stream()
                .filter(s -> s.getDay().isEqual(LocalDate.now()))
                .mapToLong(SmokedCountByDay::getCount)
                .sum();

    // Calculer firstSmokedRecorded et totalSmoked
    LocalDate firstSmokedRecorded =
        authUser.getSmokedList().stream()
            .map(smoked -> smoked.getDate().toLocalDate())
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

    int totalSmoked = authUser.getSmokedList().size();

    return new UserSmokedDto(smokedToday, smokedLastWeek, firstSmokedRecorded, totalSmoked);
  }
}
