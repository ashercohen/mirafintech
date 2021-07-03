package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record BalanceIntervalListImpl(List<Interval> intervals) implements BalanceIntervalList {

    public BalanceIntervalListImpl(List<Interval> intervals) {

        List<Interval> sortedIntervals = new ArrayList<>(intervals).stream().sorted(Comparator.comparing(com.mirafintech.prototype.model.interest.Interval::from)).toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    public static record Interval(LocalDate from, LocalDate to, BigDecimal balance) implements BalanceInterval {  //TimeInterval<BigDecimal> {

        public Interval {
            validate(from, to);
        }

        @Override
        public BigDecimal value() {
            return balance;
        }
    }
}
