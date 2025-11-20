package eu.nicosworld.stoptaclop.infrastructure.web.dto;

import java.util.List;

/** Behavioral statistics about the user's smoking activity. */
public record SmokingStatsDto(
    int smokedToday, List<SmokedCountByDay> smokedLastWeek, Long minutesSinceLastSmoked) {}
