package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.interest.APR;
import com.mirafintech.prototype.model.interest.BalanceIntervals;
import com.mirafintech.prototype.model.interest.APRInterestIntervals;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;


public class InterestCalculatingEngineTest {

    private InterestCalculatingEngine engine = new InterestCalculatingEngine();

    @Test
    void calculateTest() {
        //public BigDecimal calculate(final BalanceIntervals balanceIntervals, final InterestIntervals interestIntervals) {

        BalanceIntervals balanceIntervals = new BalanceIntervals(List.of(
                new BalanceIntervals.Interval(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), new BigDecimal(1000))
        ));

        APRInterestIntervals annualInterestIntervals = new APRInterestIntervals(List.of(
                new APRInterestIntervals.Interval(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), new APR(new BigDecimal("0.1"), BigDecimal.ZERO))
        ));

        BigDecimal interest = engine.calculate(balanceIntervals, annualInterestIntervals);

        System.out.println(interest);
    }

    @Test
    void precisionTest() {

        BigDecimal one = new BigDecimal(1);
        BigDecimal two = new BigDecimal(2);
        BigDecimal three = new BigDecimal(3);

        System.out.println("one.divide(three, 4, RoundingMode.HALF_UP) = " + one.divide(three, 4, RoundingMode.HALF_UP));
        System.out.println("one.divide(three, 4, RoundingMode.HALF_EVEN) = " + one.divide(three, 4, RoundingMode.HALF_EVEN));
        System.out.println("one.divide(three, 4, RoundingMode.HALF_DOWN) = " + one.divide(three, 4, RoundingMode.HALF_DOWN));
        System.out.println("one.divide(three, 4, RoundingMode.CEILING) = " + one.divide(three, 4, RoundingMode.CEILING));
        System.out.println("one.divide(three, 4, RoundingMode.FLOOR) = " + one.divide(three, 4, RoundingMode.FLOOR));
        System.out.println("one.divide(three, 4, RoundingMode.UP) = " + one.divide(three, 4, RoundingMode.UP));
        System.out.println("one.divide(three, 4, RoundingMode.DOWN) = " + one.divide(three, 4, RoundingMode.DOWN));


        System.out.println("two.divide(three, 4, RoundingMode.HALF_UP) = " + two.divide(three, 4, RoundingMode.HALF_UP));
        System.out.println("two.divide(three, 4, RoundingMode.HALF_EVEN) = " + two.divide(three, 4, RoundingMode.HALF_EVEN));
        System.out.println("two.divide(three, 4, RoundingMode.HALF_DOWN) = " + two.divide(three, 4, RoundingMode.HALF_DOWN));
        System.out.println("two.divide(three, 4, RoundingMode.CEILING) = " + two.divide(three, 4, RoundingMode.CEILING));
        System.out.println("two.divide(three, 4, RoundingMode.FLOOR) = " + two.divide(three, 4, RoundingMode.FLOOR));
        System.out.println("two.divide(three, 4, RoundingMode.UP) = " + two.divide(three, 4, RoundingMode.UP));
        System.out.println("two.divide(three, 4, RoundingMode.DOWN) = " + two.divide(three, 4, RoundingMode.DOWN));

    }
}