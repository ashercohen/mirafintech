package com.mirafintech.prototype.model.interest;


public sealed interface DailyInterestInterval<I extends DailyInterest> extends InterestInterval<I>
        permits DailyInterestIntervalList360.Interval, DailyInterestIntervalList365.Interval {
}
