package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record DailyInterestIntervals365(List<Interval> intervals) implements DailyInterestIntervals<DailyInterest365> {

    public DailyInterestIntervals365(List<Interval> intervals) {

        List<Interval> sortedIntervals = new ArrayList<>(intervals).stream().sorted(Comparator.comparing(TimeInterval::from)).toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    public static record Interval(LocalDate from, LocalDate to, DailyInterest365 dailyInterest365) implements TimeInterval<DailyInterest365> {

        public Interval {
            validate(from, to);
        }

        @Override
        public DailyInterest365 value() {
            return dailyInterest365;
        }
    }
}