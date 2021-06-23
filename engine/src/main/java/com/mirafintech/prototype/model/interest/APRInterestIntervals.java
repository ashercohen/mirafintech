package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record APRInterestIntervals(List<Interval> intervals) implements AnnualInterestIntervals<APR> {

    public APRInterestIntervals(List<Interval> intervals) {

        List<Interval> sortedIntervals = new ArrayList<>(intervals).stream().sorted(Comparator.comparing(TimeInterval::from)).toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    @Override
    public DailyInterestIntervals365 toDailyInterestIntervals365() {
        return new DailyInterestIntervals365(
                this.intervals.stream().map(Interval::toDailyInterestIntervals365Interval).toList()
        );
    }

    public static record Interval(LocalDate from, LocalDate to, APR apr) implements TimeInterval<APR> {

        public Interval {
            validate(from, to);
        }

        @Override
        public APR value() {
            return apr;
        }

        private DailyInterestIntervals365.Interval toDailyInterestIntervals365Interval() {
            return new DailyInterestIntervals365.Interval(this.from, this.to, this.apr.toDailyInterest365());
        }
    }
}