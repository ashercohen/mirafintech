package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;

sealed interface Interest
        permits DailyInterest, AnnualInterest {

    BigDecimal tranche();
    BigDecimal mira();
}
