package com.mirafintech.prototype.model.interest;


public sealed interface DailyInterestRate extends InterestRate
        permits DailyInterestRate360, DailyInterestRate365 {}

