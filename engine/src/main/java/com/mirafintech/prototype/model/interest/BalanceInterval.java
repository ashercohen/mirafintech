package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;


public sealed interface BalanceInterval extends Interval<BigDecimal>
        permits BalanceIntervalListImpl.Interval {
}
