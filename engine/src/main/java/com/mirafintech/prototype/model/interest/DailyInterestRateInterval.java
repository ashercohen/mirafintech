package com.mirafintech.prototype.model.interest;


public sealed interface DailyInterestRateInterval<I extends DailyInterestRate> extends InterestRateInterval<I>
        permits DailyInterestIntervalList360.Interval, DailyInterestIntervalList365.Interval {
}
