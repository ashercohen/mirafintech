package com.mirafintech.prototype.model.interest;


sealed interface InterestRate extends Interest
        permits DailyInterestRate, AnnualInterestRate {
}
