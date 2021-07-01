package com.mirafintech.prototype.model.interest;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;


public class RawIntervalTest {

    @Test
    public void intersectsTest1() {

        // overlap
        RawInterval interval = new RawInterval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-07-01"));
        RawInterval otherInterval = new RawInterval(LocalDate.parse("2021-06-20"), LocalDate.parse("2021-07-10"));

        assertTrue(interval.intersects(otherInterval));
        assertTrue(otherInterval.intersects(interval));
    }

    @Test
    public void intersectsTest2() {

        // no overlap
        RawInterval interval = new RawInterval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-07-01"));
        RawInterval otherInterval = new RawInterval(LocalDate.parse("2021-07-01"), LocalDate.parse("2021-08-01"));

        assertFalse(interval.intersects(otherInterval));
        assertFalse(otherInterval.intersects(interval));
    }

    @Test
    public void intersectsTest3() {

        // one contains the other
        RawInterval interval = new RawInterval(LocalDate.parse("2021-06-01"), LocalDate.parse("2021-07-01"));
        RawInterval otherInterval = new RawInterval(LocalDate.parse("2021-06-10"), LocalDate.parse("2021-06-20"));

        assertTrue(interval.intersects(otherInterval));
        assertTrue(otherInterval.intersects(interval));
    }
}