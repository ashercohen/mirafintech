package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;


public record DailyInterest360(BigDecimal tranche, BigDecimal mira) implements DailyInterest {

//    public static final DailyInterest365 ZERO = new DailyInterest365(BigDecimal.ZERO, BigDecimal.ZERO);
//
//    private static final BigDecimal threeSixtyFive = new BigDecimal(365);
//
//    public DailyInterest toDailyInterest() {
//        return new DailyInterest(tranche.divide(threeSixtyFive, 10, RoundingMode.HALF_UP), mira.divide(threeSixtyFive, 10, RoundingMode.DOWN));
//    }
}
