package com.mirafintech.prototype.model.interest;


sealed interface InterestIntervalList<I extends InterestRate> extends IntervalList<I>
        permits DailyInterestIntervalList, AnnualInterestIntervalList {
}
