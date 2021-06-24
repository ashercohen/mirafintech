package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;


public record DailyInterest360(BigDecimal tranche, BigDecimal mira) implements DailyInterest {
}
