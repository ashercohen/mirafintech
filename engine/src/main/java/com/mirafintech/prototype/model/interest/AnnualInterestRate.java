package com.mirafintech.prototype.model.interest;


sealed interface AnnualInterestRate extends InterestRate
        permits APR {}

