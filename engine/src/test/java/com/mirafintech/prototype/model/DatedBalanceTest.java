package com.mirafintech.prototype.model;

import com.mirafintech.prototype.model.interest.BalanceIntervalList;
import com.mirafintech.prototype.model.interest.BalanceIntervalListImpl;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class DatedBalanceTest {

    @Test
    public void getBalanceHistory1() {

        // simplest case, one date balance

        BalanceIntervalList actual = DatedBalance.getBalanceHistory(
                List.of(new DatedBalance(LocalDate.parse("2021-06-18").atStartOfDay(), BigDecimal.valueOf(100))),
                LocalDate.parse("2021-06-18"),
                LocalDate.parse("2021-06-28")
        );

        BalanceIntervalListImpl expected = new BalanceIntervalListImpl(
                List.of(
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-18"), LocalDate.parse("2021-06-28"), BigDecimal.valueOf(100))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void getBalanceHistory2() {

        // balance history starts before from

        BalanceIntervalList actual = DatedBalance.getBalanceHistory(
                List.of(new DatedBalance(LocalDate.parse("2021-06-16").atStartOfDay(), BigDecimal.valueOf(100))),
                LocalDate.parse("2021-06-18"),
                LocalDate.parse("2021-06-28")
        );

        BalanceIntervalListImpl expected = new BalanceIntervalListImpl(
                List.of(
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-18"), LocalDate.parse("2021-06-28"), BigDecimal.valueOf(100))
                )
        );

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBalanceHistory3() {

        // balance history starts after from

        DatedBalance.getBalanceHistory(
                List.of(new DatedBalance(LocalDate.parse("2021-06-30").atStartOfDay(), BigDecimal.valueOf(100))),
                LocalDate.parse("2021-06-28"),
                LocalDate.parse("2021-07-08")
        );
    }

    @Test
    public void getBalanceHistory4() {

        // balance history starts before from

        BalanceIntervalList actual = DatedBalance.getBalanceHistory(
                List.of(
                        new DatedBalance(LocalDate.parse("2021-05-25").atStartOfDay(), BigDecimal.valueOf(100)),
                        new DatedBalance(LocalDate.parse("2021-06-10").atStartOfDay(), BigDecimal.valueOf(200)),
                        new DatedBalance(LocalDate.parse("2021-06-20").atStartOfDay(), BigDecimal.valueOf(400))
                ),
                LocalDate.parse("2021-06-01"),
                LocalDate.parse("2021-07-01")
        );

        BalanceIntervalListImpl expected = new BalanceIntervalListImpl(
                List.of(
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-10"), BigDecimal.valueOf(100)),
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-20"), BigDecimal.valueOf(200)),
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-20"), LocalDate.parse("2021-07-01"), BigDecimal.valueOf(400))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void getBalanceHistory5() {

        // balance history contains balances out of [from,to) range

        BalanceIntervalList actual = DatedBalance.getBalanceHistory(
                List.of(
                        new DatedBalance(LocalDate.parse("2021-05-15").atStartOfDay(), BigDecimal.valueOf(700)),
                        new DatedBalance(LocalDate.parse("2021-05-25").atStartOfDay(), BigDecimal.valueOf(100)),
                        new DatedBalance(LocalDate.parse("2021-06-10").atStartOfDay(), BigDecimal.valueOf(200)),
                        new DatedBalance(LocalDate.parse("2021-06-20").atStartOfDay(), BigDecimal.valueOf(400))
                ),
                LocalDate.parse("2021-06-01"),
                LocalDate.parse("2021-07-01")
        );

        BalanceIntervalListImpl expected = new BalanceIntervalListImpl(
                List.of(
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-10"), BigDecimal.valueOf(100)),
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-20"), BigDecimal.valueOf(200)),
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-20"), LocalDate.parse("2021-07-01"), BigDecimal.valueOf(400))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void getBalanceHistory6() {

        // balance history contains balances out of [from,to) range: balances ends at from and starts at to

        BalanceIntervalList actual = DatedBalance.getBalanceHistory(
                List.of(
                        new DatedBalance(LocalDate.parse("2021-05-15").atStartOfDay(), BigDecimal.valueOf(700)),
                        new DatedBalance(LocalDate.parse("2021-06-01").atStartOfDay(), BigDecimal.valueOf(100)),
                        new DatedBalance(LocalDate.parse("2021-06-10").atStartOfDay(), BigDecimal.valueOf(200)),
                        new DatedBalance(LocalDate.parse("2021-06-20").atStartOfDay(), BigDecimal.valueOf(400)),
                        new DatedBalance(LocalDate.parse("2021-07-01").atStartOfDay(), BigDecimal.valueOf(900))
                ),
                LocalDate.parse("2021-06-01"),
                LocalDate.parse("2021-07-01")
        );

        BalanceIntervalListImpl expected = new BalanceIntervalListImpl(
                List.of(
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-10"), BigDecimal.valueOf(100)),
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-20"), BigDecimal.valueOf(200)),
                        new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-20"), LocalDate.parse("2021-07-01"), BigDecimal.valueOf(400))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void toIntervalsTest1() {

        // simple case - one dated balance
        LocalDateTime from = LocalDate.now().atTime(LocalTime.MIDNIGHT);
        LocalDate to = from.plusDays(10).toLocalDate();
        BigDecimal balance = BigDecimal.valueOf(100);
        List<BalanceIntervalListImpl.Interval> actual =
                DatedBalance.toIntervalStream(List.of(new DatedBalance(from, balance)), to).toList();

        List<BalanceIntervalListImpl.Interval> expected =
                List.of(new BalanceIntervalListImpl.Interval(from.toLocalDate(), to, balance));

        assertEquals(expected, actual);
    }

    @Test
    public void toIntervalsTest2() {

        List<BalanceIntervalListImpl.Interval> actual = DatedBalance.toIntervalStream(
                new ArrayList<>(List.of(
                        new DatedBalance(LocalDate.parse("2021-06-01").atStartOfDay(), BigDecimal.valueOf(100)),
                        new DatedBalance(LocalDate.parse("2021-06-20").atStartOfDay(), BigDecimal.valueOf(200))
                )),
                LocalDate.parse("2021-07-01")
        ).toList();

        List<BalanceIntervalListImpl.Interval> expected = List.of(
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-20"), BigDecimal.valueOf(100)),
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-20"), LocalDate.parse("2021-07-01"), BigDecimal.valueOf(200))
        );

        assertEquals(expected, actual);
    }

    @Test
    public void toIntervalsTest3() {

        List<BalanceIntervalListImpl.Interval> actual = DatedBalance.toIntervalStream(
                new ArrayList<>(List.of(
                        new DatedBalance(LocalDate.parse("2021-06-01").atStartOfDay(), BigDecimal.valueOf(100)),
                        new DatedBalance(LocalDate.parse("2021-06-20").atStartOfDay(), BigDecimal.valueOf(200)),
                        new DatedBalance(LocalDate.parse("2021-07-10").atStartOfDay(), BigDecimal.valueOf(300)),
                        new DatedBalance(LocalDate.parse("2021-07-15").atStartOfDay(), BigDecimal.valueOf(200))
                )),
                LocalDate.parse("2021-08-01")
        ).toList();

        List<BalanceIntervalListImpl.Interval> expected = List.of(
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-20"), BigDecimal.valueOf(100)),
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-20"), LocalDate.parse("2021-07-10"), BigDecimal.valueOf(200)),
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-07-10"), LocalDate.parse("2021-07-15"), BigDecimal.valueOf(300)),
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-07-15"), LocalDate.parse("2021-08-01"), BigDecimal.valueOf(200))
        );

        assertEquals(expected, actual);
    }

    @Test
    public void toIntervalsTest4() {

        List<BalanceIntervalListImpl.Interval> actual = DatedBalance.toIntervalStream(
                new ArrayList<>(List.of(
                        new DatedBalance(LocalDate.parse("2021-06-01").atStartOfDay(), BigDecimal.valueOf(100)),
                        new DatedBalance(LocalDate.parse("2021-06-20").atStartOfDay(), BigDecimal.valueOf(200))
                )),
                LocalDate.parse("2021-06-15")
        ).toList();

        List<BalanceIntervalListImpl.Interval> expected = List.of(
                new BalanceIntervalListImpl.Interval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-06-20"), BigDecimal.valueOf(100))
        );

        assertEquals(expected, actual);
    }
}