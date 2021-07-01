package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.interest.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;


@Entity
@Table(name = "DATED_BALANCE")
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatedBalance implements Dated<BigDecimal, BalanceIntervalListImpl.Interval, BalanceIntervalListImpl> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    private BigDecimal balance;

    private DatedBalance(Long id, LocalDateTime timestamp, BigDecimal balance) {
        this.id = id;
        this.timestamp = timestamp;
        this.balance = balance;
    }

    public DatedBalance(LocalDateTime timestamp, BigDecimal balance) {
        this(null, timestamp, balance);
    }
    // instance to call default methods from static context
    private static final DatedBalance DATED_BALANCE = new DatedBalance();

    public static BalanceIntervalListImpl getBalanceHistory(List<DatedBalance> list, LocalDate from, LocalDate to) {
        return DATED_BALANCE.getHistory(list, from, to);
    }

    public static Stream<BalanceIntervalListImpl.Interval> toIntervalStream(List<DatedBalance> sortedHistory, LocalDate to) {
        return new DatedBalance().toIntervals(sortedHistory, to);
    }

    @Override
    public BigDecimal getV() {
        return getBalance();
    }

    @Override
    public BalanceIntervalListImpl.Interval newInterval(LocalDate from, LocalDate to, BigDecimal value) {
        return new BalanceIntervalListImpl.Interval(from, to, value);
    }

    @Override
    public DatedBalance newDummyInstance(LocalDateTime timestamp) {
        return new DatedBalance(timestamp, BigDecimal.ZERO);
    }

//    @Override
//    public IntervalList<BigDecimal> newIntervalList(List<? extends Interval<BigDecimal>> intervals) {
//        return new BalanceIntervalListImpl((List<BalanceIntervalListImpl.Interval>)intervals);
//    }

    @Override
    public BalanceIntervalListImpl newIntervalList(List<BalanceIntervalListImpl.Interval> intervals) {
        return new BalanceIntervalListImpl((List<BalanceIntervalListImpl.Interval>)intervals);
    }

    //    @Override
//    public BalanceIntervalListImpl.Interval newInterval() {
//        new BalanceIntervalListImpl.Interval()
//        return null;
//    }

//    public static BalanceIntervalList getBalanceHistory(List<DatedBalance> balanceHistory, LocalDate from, LocalDate to) {
//
//        if (balanceHistory == null || balanceHistory.isEmpty()) {
//            throw new IllegalArgumentException("null or empty balance history");
//        }
//
//        ArrayList<DatedBalance> sortedHistory =
//                balanceHistory.stream()
//                        .sorted(Comparator.comparing(DatedBalance::getTimestamp))
//                        .collect(Collectors.toCollection(ArrayList::new));
//
//        verifyStartDate(sortedHistory.get(0), from);
//        RawInterval entireRange = new RawInterval(from, to);
//
//        List<BalanceIntervalListImpl.Interval> intervals =
//                toIntervals(sortedHistory, to)
//                        .filter(interval -> entireRange.intersects(interval))
//                        .collect(Collectors.toCollection(ArrayList::new));
//
//        // fix from of first interval if needed
//        if (intervals.get(0).from().isBefore(from)) {
//            BalanceIntervalListImpl.Interval remove = intervals.remove(0);
//            intervals.add(0, new BalanceIntervalListImpl.Interval(from, remove.to(), remove.balance()));
//        }
//
//        return new BalanceIntervalListImpl(intervals.stream().toList());
//    }

//    static Stream<BalanceIntervalListImpl.Interval> toIntervals(List<DatedBalance> sortedHistory, LocalDate to) {
//
//        if (sortedHistory.size() == 1) {
//            return Stream.of(new BalanceIntervalListImpl.Interval(
//                    sortedHistory.get(0).getTimestamp().toLocalDate(),
//                    to,
//                    sortedHistory.get(0).getBalance())
//            );
//        }
//
//        if (sortedHistory.get(sortedHistory.size() - 1).getTimestamp().toLocalDate().isBefore(to)) {
//            // add 'to' as dummy element to be used as the last interval's 'to'
//            sortedHistory.add(new DatedBalance(to.atStartOfDay(), BigDecimal.ZERO));
//        }
//
//        return IntStream.rangeClosed(0, sortedHistory.size() - 2)
//                .boxed()
//                .map(idx -> {
//                    DatedBalance current = sortedHistory.get(idx);
//                    DatedBalance next = sortedHistory.get(idx + 1);
//
//                    return new BalanceIntervalListImpl.Interval(
//                            current.getTimestamp().toLocalDate(),
//                            next.getTimestamp().toLocalDate(),
//                            current.getBalance()
//                    );
//                });
//    }

//    private static void verifyStartDate(DatedBalance datedBalance, LocalDate from) {
//        if (datedBalance.getTimestamp().toLocalDate().isAfter(from)) {
//            throw new IllegalArgumentException(
//                    String.format("balance history starts after 'from' parameter: balance history start=%s, from=%s",
//                            datedBalance.getTimestamp(), from));
//        }
//    }
}
