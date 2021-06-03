package com.mirafintech.prototype.service;

import com.mirafintech.prototype.event.NewDateEvent;
import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.repository.SystemTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class TimeService {

    @Autowired
    private SystemTimeRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED) // Support current transaction, create a new one if none exists
    public SystemTime setTime(LocalDateTime newDateTime) {

        SystemTime systemTime = new SystemTime(newDateTime);
        Optional<LocalDateTime> currentTime = getCurrentDateTimeInternal();
        currentTime.ifPresent(current -> {
            if (current.isAfter(newDateTime)) {
                throw new IllegalArgumentException("new time is before current time");
            }
        });

        currentTime.ifPresent((current) -> advanceMultipleDays(current, newDateTime));

        return repository.saveAndFlush(systemTime);
    }

    public SystemTime getCurrentSystemTime() {
        return this.repository.findTopByOrderByIdDesc();
    }

    public LocalDateTime getCurrentDateTime() {
        return this.getCurrentDateTimeInternal().orElseThrow(() -> new RuntimeException("system time hasn't been set"));
    }

    private Optional<LocalDateTime> getCurrentDateTimeInternal() {
        return Optional.ofNullable(this.repository.findTopByOrderByIdDesc()).map(SystemTime::getDateTime);
    }

    /**
     * perform multi day "jump"
     * for each day between current and new, emit a NewDateEvent
     */
    private void advanceMultipleDays(LocalDateTime currentTime, LocalDateTime newTime) {

        // TODO: test this:
        //  - check inclusive/exclusive
        //  - who persists newDateTime? eventHandler? this? what about the other days in this stream

        Stream.iterate(currentTime, date -> date.plusDays(1L))
                .takeWhile(date -> date.isBefore(newTime))
                .map(date -> new NewDateEvent(this, date))
                .peek(System.out::println)
                .forEach(applicationEventPublisher::publishEvent);
    }
}
