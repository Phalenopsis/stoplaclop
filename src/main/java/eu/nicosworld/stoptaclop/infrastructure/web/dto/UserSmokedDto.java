package eu.nicosworld.stoptaclop.infrastructure.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO containing various statistics about a user's smoking activity.
 *
 * @param smokedToday the number of cigarettes smoked by the user today
 * @param smokedLastWeek the list of daily cigarette counts for the past week
 * @param firstSmokedRecorded the date of the user's first recorded cigarette (or null if none)
 * @param totalSmoked the total number of cigarettes the user has recorded
 * @param lastSmokedRecorded time od the last cigarette recorded (or null if none)
 */
public record UserSmokedDto(
    int smokedToday,
    List<SmokedCountByDay> smokedLastWeek,
    LocalDate firstSmokedRecorded,
    int totalSmoked,
    LocalDateTime lastSmokedRecorded) {}
