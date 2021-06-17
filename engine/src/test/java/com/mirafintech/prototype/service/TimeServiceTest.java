package com.mirafintech.prototype.service;

import com.mirafintech.prototype.event.EndOfDayEvent;
import org.junit.Test;
import org.junit.Assert;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class TimeServiceTest {

    private record LocalDateTimeInterval(LocalDateTime currentTime, LocalDateTime newTime, int expected) {}

    @Test
    public void iterateDaysTest() {

        generateIntervals().forEach(interval -> {
            Assert.assertEquals(interval.expected(), generateEvents(interval.currentTime(), interval.newTime()).toList().size());
        });
    }

    private Stream<EndOfDayEvent> generateEvents(LocalDateTime currentTime, LocalDateTime newTime) {

        return TimeService.iterateDays(currentTime, newTime)
                .map(localDate -> new EndOfDayEvent(this, localDate))
                .peek(System.out::println);
    }

    private List<LocalDateTimeInterval> generateIntervals() {

        return List.of(
                // exactly 48 hours => 2 date changes
                new LocalDateTimeInterval(LocalDateTime.parse("2021-05-01T10:00:00"), LocalDateTime.parse("2021-05-03T10:00:00"), 2),
                // no date change => 0
                new LocalDateTimeInterval(LocalDateTime.parse("2021-05-01T00:00:00"), LocalDateTime.parse("2021-05-01T23:59:59"), 0),
                // 0 seconds => 0
                new LocalDateTimeInterval(LocalDateTime.parse("2021-05-01T10:00:00"), LocalDateTime.parse("2021-05-01T10:00:00"), 0),
                // 1 second but with date change => 1
                new LocalDateTimeInterval(LocalDateTime.parse("2021-05-01T23:59:59"), LocalDateTime.parse("2021-05-02T00:00:00"), 1)
        );
    }
}
