package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;

sealed interface Interval<V>
        permits BalanceInterval, InterestInterval, RawInterval {

    LocalDate from();
    LocalDate to();
    V value();

    default void validate(LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException(String.format("illegal interval: from=%s, to=%s", from, to));
        }
    }
}
