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

        BigDecimal miraInterest = configurationService.getMiraInterest(); // TODO: calculate mira interest

        // TODO: implement
        return BigDecimal.valueOf(10L);
    }

    private DailyInterestIntervalList<?> convertToDailyInterest(AnnualInterestIntervalList<?> annualInterestIntervals) {
        /**
         * TODO: base on config return one of the implementations of DailyInterestIntervals
         *  - DailyInterestIntervalList365
         *  - DailyInterestIntervalList360
         */
        return annualInterestIntervals.toDailyInterestIntervals365();
    }

    public BigDecimal calculate(final BalanceIntervalListImpl balanceIntervals, final AnnualInterestIntervalList<?> annualInterestIntervals) {

        RawInterval balancesDatesRange = balanceIntervals.entireDatesRange().orElseThrow(() -> new RuntimeException("empty balanceIntervals range"));
        DailyInterestIntervalList<?> dailyInterestIntervals = convertToDailyInterest(annualInterestIntervals);

        BigDecimal interest = Stream.iterate(balancesDatesRange.from(), date -> date.plusDays(1L))
                .takeWhile(date -> date.isBefore(balancesDatesRange.to()))
                .map(date -> calculateAtDate(date, balanceIntervals, dailyInterestIntervals))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("daily interest reduction failed"));

        return interest;
    }

    private BigDecimal calculateAtDate(LocalDate date, final BalanceIntervalListImpl balanceIntervals, final DailyInterestIntervalList<?> dailyInterestIntervalList) {

        BigDecimal balance =
                balanceIntervals.findByDate(date)
                        .map(BalanceInterval::value)
                        .orElseThrow(() -> new NoSuchElementException("no balance interval for date: " + date.toString()));

        BigDecimal dailyInterest =
                dailyInterestIntervalList.findByDate(date)
                        .map(InterestInterval::value)
                        .map(DailyInterest::tranche)
                        .orElseThrow(() -> new NoSuchElementException("no interest interval for date: " + date.toString()));

        return balance.multiply(dailyInterest);
    }
}
