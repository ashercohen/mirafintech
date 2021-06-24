package com.mirafintech.prototype.model.interest;


sealed interface InterestIntervalList<I extends Interest> extends IntervalList<I>
        permits DailyInterestIntervalList, AnnualInterestIntervalList {
}
