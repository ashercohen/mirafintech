package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.interest.*;
import com.mirafintech.prototype.model.loan.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.stream.Stream;


@Component
public class InterestCalculatingEngine {

    @Autowired
    private ConfigurationService configurationService;

    public BigDecimal calculate(Loan loan, Consumer consumer) {

        BigDecimal miraInterest = configurationService.getMiraInterest(); // TODO: probably not needed

        // TODO: implement
//        throw new RuntimeException("not implemented yet");
        return BigDecimal.valueOf(10L);
    }

    private DailyInterestIntervals<?> convertToDailyInterest(AnnualInterestIntervals<?> annualInterestIntervals) {
        /**
         * TODO: base on config return one of the implementations of DailyInterestIntervals
         *  currently only one is defined:
         *  - DailyInterestIntervals365
         *  should be defined:
         *  - DailyInterestIntervals360 - only defined
         */
        return annualInterestIntervals.toDailyInterestIntervals365();
    }

    public BigDecimal calculate(final BalanceIntervals balanceIntervals, final AnnualInterestIntervals<?> annualInterestIntervals) {

//        BalanceIntervals.Interval firstBalanceInterval = balanceIntervals.intervals().get(0);
//        BalanceIntervals.Interval lastBalanceInterval = balanceIntervals.intervals().get(balanceIntervals.intervals().size() - 1);
        RawTimeInterval balancesDatesRange = balanceIntervals.entireDatesRange().orElseThrow(() -> new RuntimeException("empty balanceIntervals range"));

        DailyInterestIntervals<?> dailyInterestIntervals = convertToDailyInterest(annualInterestIntervals);
//        DailyInterestIntervals365 dailyInterestIntervals365 = annualInterestIntervals.toDailyInterestIntervals365();

        BigDecimal interest = Stream.iterate(balancesDatesRange.from(), date -> date.plusDays(1L))
//                .takeWhile(Predicate.not(date -> date.isAfter(balancesDatesRange.to())))
                .takeWhile(date -> date.isBefore(balancesDatesRange.to()))
//                .map(date -> calculateAtDate(date, balanceIntervals, interestIntervals))
                .map(date -> calculateAtDate(date, balanceIntervals, dailyInterestIntervals))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("daily interest reduction failed"));

        return interest;
    }

    private BigDecimal calculateAtDate(LocalDate date, final BalanceIntervals balanceIntervals, final DailyInterestIntervals<?> dailyInterestIntervals) {

        BigDecimal balance = balanceIntervals.findByDate(date).map(TimeInterval::value).orElseThrow(() -> new NoSuchElementException("no balance interval for date: " + date.toString()));
//        BigDecimal dailyInterest = interestIntervals.findByDate(date).map(TimeInterval::value).map(APR::toDailyInterest).map(DailyInterest::tranche).get();

        BigDecimal dailyInterest =
                dailyInterestIntervals.findByDate(date)
                        .map(TimeInterval::value)
                        .map(DailyInterest::tranche)
                        .orElseThrow(() -> new NoSuchElementException("no interest interval for date: " + date.toString()));

        return balance.multiply(dailyInterest);
    }

//    private BigDecimal calculate(RawTimeInterval interval, BalanceIntervals balanceIntervals, InterestIntervals interestIntervals) {
//
//        // find the interest rate for this interval
//
//
//        return BigDecimal.ZERO;
//    }

//    List<TimeInterval.RawTimeInterval> segment(BalanceIntervals balanceIntervals, InterestIntervals interestIntervals, LocalDate from, LocalDate to) {
//
//        /**
//         * merge two lists of intervals to create an ordered list of the intervals 'to()' date
//         * then fix the list:
//         * - remove all dates that are before first balance interval 'from' date
//         * - remove all dates that are after last balance interval 'to' date
//         * - prepend first balance interval 'from' date
//         * - append last balance interval 'to' date
//         */
//
//        TimeInterval.RawTimeInterval entireBalanceRange = balanceIntervals.entireDateRange().orElseThrow();
//        TimeInterval.RawTimeInterval entireInterestRange = interestIntervals.entireDateRange().orElseThrow();
//
//        if (!entireBalanceRange.isContainedIn(entireInterestRange)) {
//            throw new RuntimeException("balance range is not contained in interest range");
//        }
//
//
//        List<LocalDate> intervalToList =
//                Stream.concat(
//                        balanceIntervals.intervals().stream().map(BalanceIntervals.Interval::to),
//                        interestIntervals.intervals().stream().map(InterestIntervals.Interval::to)
//                ).sorted(Comparator.comparing(Function.identity()))
//                 .filter(localDate -> localDate.isAfter(from))
//                 .filter(localDate -> localDate.isBefore(to))
//                 .distinct()
//                 .collect(Collectors.toCollection(ArrayList::new));
//
//        intervalToList.add(0, from);
//        intervalToList.add(to);
//
//        /**
//         * iterate using sliding window of size two and generate RawTimeInterval from each pair
//         */
//        return IntStream.rangeClosed(0, intervalToList.size() - 2)
//                    .mapToObj(idx -> new TimeInterval.RawTimeInterval(intervalToList.get(idx), intervalToList.get(idx + 1)))
//                    .toList();
//    }

}
