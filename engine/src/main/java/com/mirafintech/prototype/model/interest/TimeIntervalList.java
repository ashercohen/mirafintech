package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


public interface TimeIntervalList<V> {

    List<? extends TimeInterval<V>> intervals();

    default Optional<? extends TimeInterval<V>> findByDate(LocalDate date) {
        return intervals().stream().filter(interval -> interval.to().isAfter(date)).findFirst();
    }

    default Optional<RawTimeInterval> entireDatesRange() {

        if (intervals().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new RawTimeInterval(
                intervals().get(0).from(),
                intervals().get(intervals().size() - 1).to()
        ));
    }

    default void validate(List<? extends TimeInterval<V>> intervalList) {

        if (intervalList.isEmpty()) {
            throw new IllegalArgumentException("empty intervals list");
        }

//        // sort - copy to mutable list in case list passed is immutable
//        ArrayList<? extends TimeInterval> timeIntervals = new ArrayList<>(intervalList);
//        timeIntervals.sort(Comparator.comparing(TimeInterval::from));

        /**
         * iterate over pairs (sliding windows of size 2) and make sure that
         * - there is no overlap
         * - there is not gap
         */
        IntStream.rangeClosed(0, intervalList.size() - 2)
                .forEach(idx -> {
                    TimeInterval<V> earlier = intervalList.get(idx);
                    TimeInterval<V> later = intervalList.get(idx + 1);

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

//    //List<TimeInterval.RawTimeInterval> segment(BalanceIntervals balanceIntervals, InterestIntervals interestIntervals, LocalDate from, LocalDate to) {
//    default List<RawTimeInterval> mergeWith(TimeIntervalList other, LocalDate from, LocalDate to) {
//
//        /**
//         * merge two lists of intervals to create an ordered list of the intervals 'to()' date
//         * then fix the list:
//         * - remove all dates that are before this first interval 'from' date
//         * - remove all dates that are after this last interval 'to' date
//         * - prepend this first interval 'from' date
//         * - append this last interval 'to' date
//         */
//
//        RawTimeInterval thisEntireRange = this.entireDateRange().orElseThrow(() -> new IllegalArgumentException("this entire range is empty"));
//        RawTimeInterval otherEntireRange = other.entireDateRange().orElseThrow(() -> new IllegalArgumentException("other entire range is empty"));
//
//        if (!thisEntireRange.isContainedIn(otherEntireRange)) {
//            throw new RuntimeException("this date range is not contained in other date range");
//        }
//
//        List<LocalDate> intervalToList =
//                Stream.concat(
//                        this.intervals().stream().map(TimeInterval::to),
//                        other.intervals().stream().map(TimeInterval::to)
//                ).sorted(Comparator.comparing(Function.identity()))
//                        .filter(localDate -> localDate.isAfter(from))
//                        .filter(localDate -> localDate.isBefore(to))
//                        .distinct()
//                        .collect(Collectors.toCollection(ArrayList::new));
//
//        intervalToList.add(0, from);
//        intervalToList.add(to);
//
//        /**
//         * iterate using sliding window of size two and generate RawTimeInterval from each pair
//         */
//        return IntStream.rangeClosed(0, intervalToList.size() - 2)
//                .mapToObj(idx -> new RawTimeInterval(intervalToList.get(idx), intervalToList.get(idx + 1)))
//                .toList();
//    }
}
