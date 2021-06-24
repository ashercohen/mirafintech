package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


sealed interface IntervalList<V>
        permits InterestIntervalList, BalanceIntervalList {

    List<? extends Interval<V>> intervals();

    default Optional<? extends Interval<V>> findByDate(LocalDate date) {
        return intervals().stream().filter(interval -> interval.to().isAfter(date)).findFirst();
    }

    default Optional<RawInterval> entireDatesRange() {

        if (intervals().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new RawInterval(
                intervals().get(0).from(),
                intervals().get(intervals().size() - 1).to()
        ));
    }

    default void validate(List<? extends Interval<V>> intervalList) {

        if (intervalList.isEmpty()) {
            throw new IllegalArgumentException("empty intervals list");
        }

        /**
         * iterate over pairs (sliding windows of size 2) and make sure that
         * - there is no overlap
         * - there is not gap
         */
        IntStream.rangeClosed(0, intervalList.size() - 2)
                .forEach(idx -> {
                    Interval<V> earlier = intervalList.get(idx);
                    Interval<V> later = intervalList.get(idx + 1);

                    /**
                     * intervals must monotonic increase
                     * gaps between consecutive intervals are prohibited
                     */
                    if (!earlier.to().equals(later.from()) || earlier.equals(later)) {
                        throw new IllegalArgumentException(String.format("illegal interval pair: earlier=[%s, %s], later=[%s, %s]",
                                earlier.from(), earlier.to(), later.from(), later.to()));
                    }
                });
    }
}
