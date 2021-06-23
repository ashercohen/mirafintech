package com.mirafintech.prototype.model.interest;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class IntervalsTest {

    @Test
    public void balanceIntervalTest() {
        new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO);
    }

    @Test (expected = IllegalArgumentException.class)
    public void malformedBalanceIntervalTest() {
        new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-15"), BigDecimal.ZERO);
    }

    @Test
    public void balanceIntervalListTest() {
        new BalanceIntervals(List.of(
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-17"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-17"), LocalDate.parse("2021-06-18"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-18"), LocalDate.parse("2021-06-19"), BigDecimal.ZERO)
        ));
    }

    @Test (expected = IllegalArgumentException.class) // gaps between consecutive intervals are prohibited
    public void noGapsIntervalListTest() {
        new BalanceIntervals(List.of(
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO),
                // missing day
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-17"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-17"), LocalDate.parse("2021-06-18"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-18"), LocalDate.parse("2021-06-19"), BigDecimal.ZERO)
        ));
    }


    @Test (expected = IllegalArgumentException.class) // intervals must monotonic increase
    public void monotonicIncreasingIntervalListTest() {
        new BalanceIntervals(List.of(
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO),
                // duplicate day
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-17"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-17"), LocalDate.parse("2021-06-18"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-18"), LocalDate.parse("2021-06-19"), BigDecimal.ZERO)
        ));
    }

    @Test (expected = IllegalArgumentException.class)
    public void overlappingIntervalListTest() {
        new BalanceIntervals(List.of(
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-14"), LocalDate.parse("2021-06-15"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-17"), BigDecimal.ZERO),
                // overlapping interval
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-17"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-17"), LocalDate.parse("2021-06-18"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-18"), LocalDate.parse("2021-06-19"), BigDecimal.ZERO)
        ));
    }

    @Test (expected = IllegalArgumentException.class)
    public void zeroLengthIntervalTest() {
        new BalanceIntervals(List.of(
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-14"), LocalDate.parse("2021-06-15"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO),
                // from=to
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-16"), BigDecimal.ZERO),
                new BalanceIntervals.Interval(LocalDate.parse("2021-06-16"), LocalDate.parse("2021-06-17"), BigDecimal.ZERO)
        ));
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyIntervalListTest() {
        new BalanceIntervals(List.of(
        ));
    }

//    @Test
//    public void mergeWithTest() {
//
//        BalanceIntervals balanceIntervals = new BalanceIntervals(List.of(
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-05"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-05"), LocalDate.parse("2021-06-15"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-22"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-27"), BigDecimal.ZERO)
//        ));
//
//        InterestIntervals interestIntervals = new InterestIntervals(List.of(
//                new InterestIntervals.Interval(LocalDate.parse("2021-05-25"), LocalDate.parse("2021-06-10"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-22"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-25"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-25"), LocalDate.parse("2021-06-30"), APR.ZERO)
//        ));
//
//        List<RawTimeInterval> actual = balanceIntervals.mergeWith(interestIntervals, LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-27"));
//
//        List<RawTimeInterval> expected = List.of(
//                new RawTimeInterval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-05")),
//                new RawTimeInterval(LocalDate.parse("2021-06-05"), LocalDate.parse("2021-06-10")),
//                new RawTimeInterval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-15")),
//                new RawTimeInterval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-22")),
//                new RawTimeInterval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-25")),
//                new RawTimeInterval(LocalDate.parse("2021-06-25"), LocalDate.parse("2021-06-27"))
//        );
//
//        assertEquals(expected, actual);
//    }
//
//    @Test (expected = RuntimeException.class) // interest intervals must "cover" entire range of balance intervals
//    public void malformedMergeWithTest() {
//
//        BalanceIntervals balanceIntervals = new BalanceIntervals(List.of(
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-05"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-05"), LocalDate.parse("2021-06-15"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-22"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-27"), BigDecimal.ZERO)
//        ));
//
//        InterestIntervals interestIntervals = new InterestIntervals(List.of(
////                new InterestIntervals.Interval(LocalDate.parse("2021-05-25"), LocalDate.parse("2021-06-10"), BigDecimal.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-22"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-25"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-25"), LocalDate.parse("2021-06-30"), APR.ZERO)
//        ));
//
//        balanceIntervals.mergeWith(interestIntervals, LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-27"));
//    }
//
//    @Test (expected = RuntimeException.class) // interest intervals must "cover" entire range of balance intervals
//    public void malformedMergeWithTest2() {
//
//        BalanceIntervals balanceIntervals = new BalanceIntervals(List.of(
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-05"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-05"), LocalDate.parse("2021-06-15"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-15"), LocalDate.parse("2021-06-22"), BigDecimal.ZERO),
//                new BalanceIntervals.Interval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-27"), BigDecimal.ZERO)
//        ));
//
//        InterestIntervals interestIntervals = new InterestIntervals(List.of(
//                new InterestIntervals.Interval(LocalDate.parse("2021-05-25"), LocalDate.parse("2021-06-10"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-22"), APR.ZERO),
//                new InterestIntervals.Interval(LocalDate.parse("2021-06-22"), LocalDate.parse("2021-06-25"), APR.ZERO)
////                new InterestIntervals.Interval(LocalDate.parse("2021-06-25"), LocalDate.parse("2021-06-30"), APR.ZERO)
//        ));
//
//        balanceIntervals.mergeWith(interestIntervals, LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-27"));
//    }
}