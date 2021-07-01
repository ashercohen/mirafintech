package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;

sealed interface InterestRate
        permits DailyInterestRate, AnnualInterestRate {

    BigDecimal tranche();
    BigDecimal mira();
}
