package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record DailyInterestIntervalList360(List<Interval> intervals) implements DailyInterestIntervalList<DailyInterestRate360> {

    public DailyInterestIntervalList360(List<Interval> intervals) {

        List<Interval> sortedIntervals =
                new ArrayList<>(intervals).stream()
                        .sorted(Comparator.comparing(com.mirafintech.prototype.model.interest.Interval::from))
                        .toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    public static record Interval(LocalDate from, LocalDate to, DailyInterestRate360 dailyInterest360) implements DailyInterestRateInterval<DailyInterestRate360> {

        public Interval {
            validate(from, to);
        }

        @Override
        public DailyInterestRate360 value() {
            return dailyInterest360;
        }
    }
}
