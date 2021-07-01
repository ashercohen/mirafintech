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
    public BigDecimal calculate(Loan loan, RawInterval calculationInterval) {

//        this interest calculated, is it for a single loan or is it for a single consumer? I think consumer - so remove the loan argument and make sure this method is not called
//                multiple times for a single consumer with multiple loans


        BigDecimal miraInterest = configurationService.getMiraInterest(); // TODO: calculate mira interest

//        BalanceIntervalList balanceIntervalList = consumer.getBalanceIntervalList(from, to);
        BalanceIntervalList balanceIntervalList = loan.balanceIntervalList(calculationInterval);
        AnnualInterestIntervalList<?> aprInterestIntervalList = loan.interestIntervalList(calculationInterval);

        // TODO: update APR with mira interest

        BigDecimal bigDecimal = doCalculate(balanceIntervalList, aprInterestIntervalList);
        // TODO: implement
        return BigDecimal.valueOf(10L);
    }

    private void prepareBalanceIntervals(Consumer consumer) {

//        consumer.getBalance()

    }

    BigDecimal doCalculate(final BalanceIntervalList balanceIntervals, final AnnualInterestIntervalList<?> annualInterestIntervals) {

        RawInterval balancesDatesRange = balanceIntervals.entireDatesRange().orElseThrow(() -> new RuntimeException("empty balanceIntervals range"));
        DailyInterestIntervalList<?> dailyInterestIntervals = convertToDailyInterest(annualInterestIntervals);

        BigDecimal interest = Stream.iterate(balancesDatesRange.from(), date -> date.plusDays(1L))
                .takeWhile(date -> date.isBefore(balancesDatesRange.to()))
                .map(date -> calculateAtDate(date, balanceIntervals, dailyInterestIntervals))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("daily interest reduction failed"));

        return interest;
    }

    private DailyInterestIntervalList<?> convertToDailyInterest(AnnualInterestIntervalList<?> annualInterestIntervals) {
        /**
         * TODO: base on config return one of the implementations of DailyInterestIntervals
         *  - DailyInterestIntervalList365
         *  - DailyInterestIntervalList360
         */
        return annualInterestIntervals.toDailyInterestIntervals365();
    }

    private BigDecimal calculateAtDate(LocalDate date, final BalanceIntervalList balanceIntervals, final DailyInterestIntervalList<?> dailyInterestIntervalList) {

        BigDecimal balance =
                balanceIntervals.findByDate(date)
                        .map(BalanceInterval::value)
                        .orElseThrow(() -> new NoSuchElementException("no balance interval for date: " + date.toString()));

        BigDecimal dailyInterest =
                dailyInterestIntervalList.findByDate(date)
                        .map(InterestRateInterval::value)
                        .map(DailyInterestRate::tranche)
                        .orElseThrow(() -> new NoSuchElementException("no interest interval for date: " + date.toString()));

        return balance.multiply(dailyInterest); // TODO: split into tranche + mira
    }
}
