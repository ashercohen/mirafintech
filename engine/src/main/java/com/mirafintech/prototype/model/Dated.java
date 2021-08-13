package com.mirafintech.prototype.model;

import com.mirafintech.prototype.model.interest.Interval;
import com.mirafintech.prototype.model.interest.IntervalList;
import com.mirafintech.prototype.model.interest.RawInterval;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public interface Dated<V, I extends Interval<V>, L extends IntervalList<V>> { // add type parameters to Interval and IntervalList

    LocalDateTime getTimestamp();

    V getV();

    I newInterval(LocalDate from, LocalDate to, V value);

    <T extends Dated<V,I,L>> T newDummyInstance(LocalDateTime timestamp);

    L newIntervalList(List<I> intervals);

    //static BalanceIntervalList getBalanceHistory(List<DatedBalance> valueHistory, LocalDate from, LocalDate to);
    default L getHistory(List<? extends Dated<V,I,L>> valueHistory, LocalDate from, LocalDate to) {

        if (valueHistory == null || valueHistory.isEmpty()) {
            throw new IllegalArgumentException("null or empty history");
        }

        ArrayList<Dated<V, I, L>> sortedHistory =
                valueHistory.stream()
                        .sorted(Comparator.comparing(Dated::getTimestamp/*DatedBalance::getTimestamp*/))
                        .collect(Collectors.toCollection(ArrayList::new));

        verifyStartDate(sortedHistory.get(0), from);
        RawInterval entireRange = new RawInterval(from, to);

        List<I> intervals =
                toIntervals(sortedHistory, to)
                        .filter(interval -> entireRange.intersects(interval))
                        .collect(Collectors.toCollection(ArrayList::new));

        // fix from of first interval if needed
        if (intervals.get(0).from().isBefore(from)) {
            Interval<V> remove = intervals.remove(0);
            intervals.add(0, newInterval(from, remove.to(), remove.value()));
        }

        return newIntervalList(intervals.stream().toList());
    }

    default <T extends Dated<V, I, L>> Stream<I> toIntervals(List<T> sortedHistory, LocalDate to) {

        if (sortedHistory.size() == 1) {

            return Stream.of(newInterval(
                            sortedHistory.get(0).getTimestamp().toLocalDate(),
                            to,
                            sortedHistory.get(0).getV())
            );
        }

        // for each date in the sortedHistory, take the latest value/entry (in case there is more than one)
        TreeMap<LocalDate, List<T>> balanceByDate = sortedHistory.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getTimestamp().toLocalDate(),
                        TreeMap::new,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ));

        List<T> history = balanceByDate.values().stream()
                .map(l -> l.stream().max(Comparator.comparing(Dated::getTimestamp)))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(Dated::getTimestamp))
                .collect(Collectors.toCollection(ArrayList::new));


        if (history.get(history.size() - 1).getTimestamp().toLocalDate().isBefore(to)) {
            // add 'to' as dummy element to be used as the last interval's 'to'
            history.add(newDummyInstance(to.atStartOfDay()));
        }

        return IntStream.rangeClosed(0, history.size() - 2)
                .boxed()
                .map(idx -> {
                    T current = history.get(idx);
                    T next = history.get(idx + 1);

                    return newInterval(
                            current.getTimestamp().toLocalDate(),
                            next.getTimestamp().toLocalDate(),
                            current.getV()
                    );
                });
    }

    private void verifyStartDate(Dated<V, I, L> dated, LocalDate from) {
        if (dated.getTimestamp().toLocalDate().isAfter(from)) {
            throw new IllegalArgumentException(
                    String.format("balance history starts after 'from' parameter: balance history start=%s, from=%s",
                            dated.getTimestamp(), from));
        }
    }
}
