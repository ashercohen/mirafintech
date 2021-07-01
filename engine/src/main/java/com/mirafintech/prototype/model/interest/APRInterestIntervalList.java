package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record APRInterestIntervalList(List<Interval> intervals) implements AnnualInterestIntervalList<APR> {

    public APRInterestIntervalList(List<Interval> intervals) {

        List<Interval> sortedIntervals =
                new ArrayList<>(intervals).stream()
                        .sorted(Comparator.comparing(com.mirafintech.prototype.model.interest.Interval::from))
                        .toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    @Override
    public DailyInterestIntervalList360 toDailyInterestIntervals360() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public DailyInterestIntervalList365 toDailyInterestIntervals365() {
        return new DailyInterestIntervalList365(
                this.intervals.stream().map(APRInterestIntervalList.Interval::toDailyInterestIntervals365Interval).toList()
        );
    }

    public static record Interval(LocalDate from, LocalDate to, APR apr) implements AnnualInterestRateInterval<APR> {

        public Interval {
            validate(from, to);
        }

        @Override
        public APR value() {
            return apr;
        }

        private DailyInterestIntervalList365.Interval toDailyInterestIntervals365Interval() {
            return new DailyInterestIntervalList365.Interval(this.from, this.to, this.apr.toDailyInterest365());
        }
    }
}