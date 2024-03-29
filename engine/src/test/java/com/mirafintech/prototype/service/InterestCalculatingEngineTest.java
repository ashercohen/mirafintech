package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.interest.APR;
import com.mirafintech.prototype.model.interest.BalanceIntervalListImpl;
import com.mirafintech.prototype.model.interest.APRInterestIntervalList;
import com.mirafintech.prototype.model.interest.CalculatedInterest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;


public class InterestCalculatingEngineTest {

    private InterestCalculatingEngine engine = new InterestCalculatingEngine();

    @Test
    void calculateTest() {

        BalanceIntervalListImpl balanceIntervals = new BalanceIntervalListImpl(List.of(
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), new BigDecimal(1000))
        ));

        APRInterestIntervalList annualInterestIntervals = new APRInterestIntervalList(List.of(
                new APRInterestIntervalList.Interval(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), new APR(new BigDecimal("0.1"), new BigDecimal("0.005")))
        ));

        CalculatedInterest interest = engine.doCalculate(balanceIntervals, annualInterestIntervals);

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