package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;


sealed interface Interest
        permits InterestRate, CalculatedInterest {

    BigDecimal tranche();
    BigDecimal mira();
}
