package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;

/**
 * aggregate class for calculated interest (not rate!)
 */
// TODO: maybe add RawInterval???
public record CalculatedInterest(BigDecimal tranche, BigDecimal mira) implements Interest {
}
