package com.mirafintech.prototype.model.interest;


public sealed interface AnnualInterestInterval<I extends AnnualInterest> extends InterestInterval<I>
        permits APRInterestIntervalList.Interval {
}
