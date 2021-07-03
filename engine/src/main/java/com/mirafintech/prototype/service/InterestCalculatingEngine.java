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

    /**
     * calculate interest for a loan
     */
    public CalculatedInterest calculate(Loan loan, RawInterval calculationInterval) {

        /**
         * loans do not carry interest for the first billing cycle
         */
        if (loan.getCreationDate().toLocalDate().isAfter(calculationInterval.from())) {
            return CalculatedInterest.ZERO;
        }

        BigDecimal miraInterest = configurationService.getMiraInterest();

        BalanceIntervalList balanceIntervalList = loan.balanceIntervalList(calculationInterval);
        AnnualInterestIntervalList<?> aprInterestIntervalList = loan.interestIntervalList(calculationInterval, miraInterest);

        return doCalculate(balanceIntervalList, aprInterestIntervalList);
    }

    CalculatedInterest doCalculate(final BalanceIntervalList balanceIntervals, final AnnualInterestIntervalList<?> annualInterestIntervals) {

        RawInterval balancesDatesRange = balanceIntervals.entireDatesRange().orElseThrow(() -> new RuntimeException("empty balanceIntervals range"));
        DailyInterestIntervalList<?> dailyInterestIntervals = convertToDailyInterest(annualInterestIntervals);

        final CalculatedInterest calculatedInterest = Stream.iterate(balancesDatesRange.from(), date -> date.plusDays(1L))
                .takeWhile(date -> date.isBefore(balancesDatesRange.to()))
                .map(date -> calculateAtDate(date, balanceIntervals, dailyInterestIntervals))
                .reduce(CalculatedInterest::add)
                .orElseThrow(() -> new RuntimeException("daily interest reduction failed"));

        return calculatedInterest;
    }

    private DailyInterestIntervalList<?> convertToDailyInterest(AnnualInterestIntervalList<?> annualInterestIntervals) {
        /**
         * TODO: base on config return one of the implementations of DailyInterestIntervals
         *  - DailyInterestIntervalList365
         *  - DailyInterestIntervalList360
         */
        return annualInterestIntervals.toDailyInterestIntervals365();
    }

    private CalculatedInterest calculateAtDate(LocalDate date, final BalanceIntervalList balanceIntervals, final DailyInterestIntervalList<?> dailyInterestIntervalList) {

        BigDecimal balance =
                balanceIntervals.findByDate(date)
                        .map(BalanceInterval::value)
                        .orElseThrow(() -> new NoSuchElementException("no balance interval for date: " + date.toString()));

        final DailyInterestRate dailyInterest = dailyInterestIntervalList.findByDate(date)
                .map(InterestRateInterval::value)
                .orElseThrow(() -> new NoSuchElementException("no interest interval for date: " + date.toString()));

        return new CalculatedInterest(dailyInterest, balance);


//        BigDecimal dailyInterest =
//                dailyInterestIntervalList.findByDate(date)
//                        .map(InterestRateInterval::value)
//                        .map(DailyInterestRate::tranche)
//                        .orElseThrow(() -> new NoSuchElementException("no interest interval for date: " + date.toString()));

//        return balance.multiply(dailyInterest); // TODO: split into tranche + mira
    }
}
