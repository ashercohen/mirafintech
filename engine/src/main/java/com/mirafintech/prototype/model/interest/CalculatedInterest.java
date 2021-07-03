package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;

/**
 * aggregate class for calculated interest (not rate!)
 */
// TODO: maybe add RawInterval???
public record CalculatedInterest(BigDecimal tranche, BigDecimal mira) implements Interest {

    public static final CalculatedInterest ZERO = new CalculatedInterest(BigDecimal.ZERO, BigDecimal.ZERO);

    public CalculatedInterest(InterestRate rate, BigDecimal balance) {
        this(rate.tranche().multiply(balance), rate.mira().multiply(balance));
    }

    public CalculatedInterest add(CalculatedInterest other) {
        return new CalculatedInterest(this.tranche.add(other.tranche), this.mira.add(other.mira));
    }

    public BigDecimal total() {
        return tranche.add(mira);
    }
}
