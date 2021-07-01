package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;


public record DailyInterestRate360(BigDecimal tranche, BigDecimal mira) implements DailyInterestRate {
}
