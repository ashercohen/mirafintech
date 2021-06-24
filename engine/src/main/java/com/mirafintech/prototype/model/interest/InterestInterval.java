package com.mirafintech.prototype.model.interest;


public sealed interface InterestInterval<I extends Interest> extends Interval<I>
        permits AnnualInterestInterval, DailyInterestInterval {
}
