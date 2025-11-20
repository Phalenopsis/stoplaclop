package eu.nicosworld.stoptaclop.infrastructure.web.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.nicosworld.stoptaclop.domain.smoked.SmokeService;
import eu.nicosworld.stoptaclop.domain.smoked.SmokedStatsService;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.UserSmokingStatsDto;

@RestController
@RequestMapping("smoked")
public class SmokedController {

  private final SmokeService smokeService;
  private final SmokedStatsService statsService;

  public SmokedController(SmokeService smokeService, SmokedStatsService smokedStatsService) {
    this.smokeService = smokeService;
    statsService = smokedStatsService;
  }

  @GetMapping
  public UserSmokingStatsDto getSmoked(@AuthenticationPrincipal UserDetails userDetails) {
    return this.statsService.getUserSmokingStats(userDetails);
  }

  @PostMapping
  public UserSmokingStatsDto smokeCigarette(@AuthenticationPrincipal UserDetails userDetails) {
    this.smokeService.smokeACigarette(userDetails);
    return this.statsService.getUserSmokingStats(userDetails);
  }
}
