package com.mirafintech.prototype.model;

import com.mirafintech.prototype.model.interest.Interval;
import com.mirafintech.prototype.model.interest.IntervalList;
import com.mirafintech.prototype.model.interest.RawInterval;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
//            BalanceIntervalListImpl.Interval remove = intervals.remove(0);
            Interval<V> remove = intervals.remove(0);
//            intervals.add(0, new BalanceIntervalListImpl.Interval(from, remove.to(), remove.balance()));
            intervals.add(0, newInterval(from, remove.to(), remove.value()));
        }

//        return new BalanceIntervalListImpl(intervals.stream().toList());
        return newIntervalList(intervals.stream().toList());
    }

    default <T extends Dated<V, I, L>> Stream<I> toIntervals(List<T> sortedHistory, LocalDate to) {

        if (sortedHistory.size() == 1) {

            return Stream.of(newInterval(
                            sortedHistory.get(0).getTimestamp().toLocalDate(),
                            to,
                            sortedHistory.get(0).getV())
            );

//            return Stream.of(new Interval(
//                    sortedHistory.get(0).getTimestamp().toLocalDate(),
//                    to,
//                    sortedHistory.get(0).getV())
//            );
        }

        if (sortedHistory.get(sortedHistory.size() - 1).getTimestamp().toLocalDate().isBefore(to)) {
            // add 'to' as dummy element to be used as the last interval's 'to'
//            sortedHistory.add(new DatedBalance(to.atStartOfDay(), BigDecimal.ZERO));
            sortedHistory.add(newDummyInstance(to.atStartOfDay()));
        }

        return IntStream.rangeClosed(0, sortedHistory.size() - 2)
                .boxed()
                .map(idx -> {
//                    DatedBalance current = sortedHistory.get(idx);
                    T current = sortedHistory.get(idx);
//                    DatedBalance next = sortedHistory.get(idx + 1);
                    T next = sortedHistory.get(idx + 1);

                    return newInterval(
                            current.getTimestamp().toLocalDate(),
                            next.getTimestamp().toLocalDate(),
                            current.getV()
                    );

//                    return new BalanceIntervalListImpl.Interval(
//                            current.getTimestamp().toLocalDate(),
//                            next.getTimestamp().toLocalDate(),
//                            current.getBalance()
//                    );
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
