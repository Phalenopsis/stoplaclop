package eu.nicosworld.stoptaclop.domain.smoked;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.nicosworld.stoptaclop.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.stoptaclop.domain.saving.Saving;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.repository.SmokedRepository;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokingStatsDto;

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

  public UserSmokingStatsDto getUserSmokingStats(UserDetails userDetails) {
    AuthenticatedUser user = authenticatedUserService.findByUser(userDetails);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneWeekAgo = now.minusDays(6);

    // Récupération des stats de la semaine
    List<SmokedCountByDay> smokedLastWeek = smokedRepository.countSmokedByDay(user, oneWeekAgo);

    LocalDateTime lastSmokedDate = smokedRepository.findLastSmokedDate(user);
    LocalDateTime firstSmokedDate = smokedRepository.findFirstSmokedDate(user);

    // Nombre total et première cigarette
    Long totalSmoked = smokedRepository.countTotalSmokedByUser(user);
    Long totalCigarettesSmoked = totalSmoked != null ? totalSmoked : 0L;

    Saving saving =
        new Saving(
            totalCigarettesSmoked,
            firstSmokedDate != null ? firstSmokedDate.toLocalDate() : now.toLocalDate());

    // Mapper final
    return UserSmokingStatsMapper.mapToDto(saving, user, smokedLastWeek, lastSmokedDate);
  }
}
