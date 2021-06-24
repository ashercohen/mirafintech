package com.mirafintech.prototype.model.interest;


public sealed interface AnnualInterestIntervalList<I extends AnnualInterest> extends InterestIntervalList<I>
        permits APRInterestIntervalList {

    DailyInterestIntervalList360 toDailyInterestIntervals360();
    DailyInterestIntervalList365 toDailyInterestIntervals365();
}
