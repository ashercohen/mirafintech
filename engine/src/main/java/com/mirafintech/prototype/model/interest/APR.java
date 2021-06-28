package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;
import java.math.RoundingMode;


public record APR(BigDecimal tranche, BigDecimal mira) implements AnnualInterest {

    public static final APR ZERO = new APR(BigDecimal.ZERO, BigDecimal.ZERO);

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int PRECISION = 10;
    private static final BigDecimal threeSixtyFive = new BigDecimal(365);
    private static final BigDecimal threeSixty = new BigDecimal(360);


    public DailyInterest365 toDailyInterest365() {
        return new DailyInterest365(
                this.tranche.divide(threeSixtyFive, PRECISION, ROUNDING_MODE),
                this.mira.divide(threeSixtyFive, PRECISION, ROUNDING_MODE)
        );
    }

    public DailyInterest360 toDailyInterest360() {
        return new DailyInterest360(
                this.tranche.divide(threeSixty, PRECISION, ROUNDING_MODE),
                this.mira.divide(threeSixty, PRECISION, ROUNDING_MODE)
        );
    }
}
