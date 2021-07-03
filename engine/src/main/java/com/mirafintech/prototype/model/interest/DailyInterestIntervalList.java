package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.Optional;


public sealed interface DailyInterestIntervalList<I extends DailyInterestRate> extends InterestIntervalList<I>
        permits DailyInterestIntervalList360, DailyInterestIntervalList365 {

    @Override
    default Optional<? extends InterestRateInterval<I>> findByDate(LocalDate date) {
        return InterestIntervalList.super.findByDate(date).map(x -> (InterestRateInterval<I>) x);
    }
}
