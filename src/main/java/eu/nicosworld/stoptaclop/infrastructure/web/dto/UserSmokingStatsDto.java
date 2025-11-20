package eu.nicosworld.stoptaclop.infrastructure.web.dto;

/**
 * DTO containing smoking statistics for a user, including both behavioral and financial
 * information.
 */
public record UserSmokingStatsDto(FinancialStatsDto financial, SmokingStatsDto stats) {}
