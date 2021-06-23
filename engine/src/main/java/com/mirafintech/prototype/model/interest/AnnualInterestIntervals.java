package com.mirafintech.prototype.model.interest;


public interface AnnualInterestIntervals<I extends AnnualInterest> extends InterestIntervals<I> {

    DailyInterestIntervals365 toDailyInterestIntervals365();
}
