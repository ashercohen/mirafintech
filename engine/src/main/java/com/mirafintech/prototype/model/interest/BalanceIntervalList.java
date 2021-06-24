package com.mirafintech.prototype.model.interest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;


sealed interface BalanceIntervalList extends IntervalList<BigDecimal>
        permits BalanceIntervalListImpl {

    @Override
    default Optional<BalanceInterval> findByDate(LocalDate date) {
        return IntervalList.super.findByDate(date).map(x -> (BalanceInterval) x);
    }
}
