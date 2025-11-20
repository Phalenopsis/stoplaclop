package eu.nicosworld.stoptaclop.infrastructure.web.dto;

/** Financial information about money saved or spent on cigarettes. */
public record FinancialStatsDto(double burnedMoney, double savedMoney, double savedMoneyToday) {}
