package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record DailyInterestIntervalList365(List<Interval> intervals) implements DailyInterestIntervalList<DailyInterestRate365> {

    public DailyInterestIntervalList365(List<Interval> intervals) {

        List<Interval> sortedIntervals =
                new ArrayList<>(intervals).stream()
                        .sorted(Comparator.comparing(com.mirafintech.prototype.model.interest.Interval::from))
                        .toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    public static record Interval(LocalDate from, LocalDate to, DailyInterestRate365 dailyInterest365) implements DailyInterestRateInterval<DailyInterestRate365> {

        public Interval {
            validate(from, to);
        }

        @Override
        public DailyInterestRate365 value() {
            return dailyInterest365;
        }
    }
}