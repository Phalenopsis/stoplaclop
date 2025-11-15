package eu.nicosworld.stoptaclop.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.List;

public record UserSmokedDto(
    int smokedToday,
    List<SmokedCountByDay> smokedLastWeek,
    LocalDate firstSmokedRecorded,
    int totalSmoked) {}
