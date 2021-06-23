package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record BalanceIntervals(List<Interval> intervals) implements TimeIntervalList<BigDecimal> {

    public BalanceIntervals(List<Interval> intervals) {

        List<Interval> sortedIntervals = new ArrayList<>(intervals).stream().sorted(Comparator.comparing(TimeInterval::from)).toList();
        validate(sortedIntervals);

        this.intervals = sortedIntervals;
    }

    public static record Interval(LocalDate from, LocalDate to, BigDecimal balance) implements TimeInterval<BigDecimal> {

        public Interval {
            validate(from, to);
        }

        @Override
        public BigDecimal value() {
            return balance;
        }
    }

//    public BalanceIntervals repartition(List<RawTimeInterval> rawTimeIntervals) {
//
////        not clear how this would be used - how the rv of this method would be used
//
//        rawTimeIntervals.indexOf()
//
//        Iterator<Interval> balanceIntervalsIterator = this.intervals.iterator();
////        Iterator<? extends TimeInterval> thisIterator = this.intervals().iterator();
//        Iterator<RawTimeInterval> rawTimeIntervalIterator = rawTimeIntervals.iterator();
//
//        while (balanceIntervalsIterator.hasNext() /*&& rawTimeIntervalIterator.hasNext()*/) {
//
//            Interval balanceInterval = balanceIntervalsIterator.next();
////            TimeInterval thisInterval = thisIterator.next();
//
//            while (rawTimeIntervalIterator.hasNext()) {
//
//                RawTimeInterval rawTimeInterval = rawTimeIntervalIterator.next();
//
//                if ()
//
//                if (rawTimeInterval.to().isAfter(thisInterval.to())) {
//                    break;
//                }
//
//                ctor.apply(rawTimeInterval, thisInterval.)
//
//
//
//
//            }
//
//        }
//
//    }
}
