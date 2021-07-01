package com.mirafintech.prototype.model.interest;


public sealed interface InterestRateInterval<I extends InterestRate> extends Interval<I>
        permits AnnualInterestRateInterval, DailyInterestRateInterval {
}
