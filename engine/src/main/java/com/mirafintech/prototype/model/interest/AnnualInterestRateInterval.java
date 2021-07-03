package com.mirafintech.prototype.model.interest;


public sealed interface AnnualInterestRateInterval<I extends AnnualInterestRate> extends InterestRateInterval<I>
        permits APRInterestIntervalList.Interval {
}
