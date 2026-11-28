package com.ayurveda.common;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

/**
 * Dashboard filter window. {@link #range()} returns the inclusive [from, to] dates
 * for the current week (Mon–Sun), month, or year.
 */
public enum DashboardPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY;

    public LocalDate[] range() {
        LocalDate today = LocalDate.now();
        return switch (this) {
            case WEEKLY -> new LocalDate[] {
                    today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                    today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)) };
            case MONTHLY -> {
                YearMonth month = YearMonth.now();
                yield new LocalDate[] { month.atDay(1), month.atEndOfMonth() };
            }
            case YEARLY -> {
                Year year = Year.now();
                yield new LocalDate[] { year.atDay(1), year.atMonth(12).atEndOfMonth() };
            }
        };
    }
}
