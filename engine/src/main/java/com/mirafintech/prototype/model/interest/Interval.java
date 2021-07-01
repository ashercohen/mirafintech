package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;

public sealed interface Interval<V>
        permits BalanceInterval, InterestRateInterval, RawInterval {

    LocalDate from(); // inclusive
    LocalDate to();   // exclusive
    V value();

    default void validate(LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException(String.format("illegal interval: from=%s, to=%s", from, to));
        }
    }
}
