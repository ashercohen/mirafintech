package com.mirafintech.prototype.service;

import com.mirafintech.prototype.event.EndOfDayEvent;
import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.repository.SystemTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class TimeService {

    @Autowired
    private SystemTimeRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public SystemTime getCurrentSystemTime() {
        return this.repository.findTopByOrderByIdDesc();
    }

    public LocalDateTime getCurrentDateTime() {
        return this.getCurrentDateTimeInternal().orElseThrow(() -> new RuntimeException("system time hasn't been set"));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED) // support current transaction, create new one if none exists
    public SystemTime setTime(LocalDateTime newSystemTime) {

        // if not an update return fast (new time same as current or time never set)
        if (isNonUpdate(newSystemTime)) {
            return getCurrentSystemTime();
        }

        // ensure we're moving forward in time
        verify(newSystemTime);

        // for each day from current to new, generate and handle synchronously 'end of day event'
        advanceMultipleDays(newSystemTime);

        // update time...
        return doSetTime(newSystemTime);
    }

    private SystemTime doSetTime(LocalDateTime newDateTime) {

        SystemTime systemTime = new SystemTime(newDateTime);
        SystemTime persistedNewSystemTime = repository.saveAndFlush(systemTime);

        // sanity check
        if (systemTime.getDateTime().compareTo(persistedNewSystemTime.getDateTime()) != 0 ||
                getCurrentDateTime().compareTo(persistedNewSystemTime.getDateTime()) != 0) {
            throw new RuntimeException("failed to update system time");
        }

        return persistedNewSystemTime;
    }

    private Optional<LocalDateTime> getCurrentDateTimeInternal() {
        return Optional.ofNullable(this.repository.findTopByOrderByIdDesc()).map(SystemTime::getDateTime);
    }

    /**
     * perform multi day "jump"
     * for each day between current and new, emit a NewDateEvent
     */
    private void advanceMultipleDays(LocalDateTime newTime) {

        // TODO: test this:
        //  - who persists newDateTime? eventHandler? this? what about the other days in this stream

        getCurrentDateTimeInternal().ifPresent(currentTime ->
                iterateDays(currentTime, newTime)
                        .map(localDate -> new EndOfDayEvent(this, localDate))
                        .peek(event -> System.out.printf("generating event: nanosOfDay=%d event=%s\n", LocalTime.now().toNanoOfDay(), event))
                        .forEachOrdered(applicationEventPublisher::publishEvent)
        );
    }

    /**
     * generate a stream of LocalDate for each date change between current and new
     * unit test exists (hence protected)
     */
    protected static Stream<LocalDate> iterateDays(LocalDateTime currentDateTime, LocalDateTime newDateTime) {
        return Stream.iterate(currentDateTime.toLocalDate(), date -> date.plusDays(1L))
                .takeWhile(date -> date.isBefore(newDateTime.toLocalDate()));
    }

    private void verify(LocalDateTime newSystemTime) {
        getCurrentDateTimeInternal().ifPresent(currentDate -> {
            if (currentDate.isAfter(newSystemTime)) {
                throw new IllegalArgumentException("new time is before current time");
            }
        });
    }

    private boolean isNonUpdate(LocalDateTime newSystemTime) {
        return getCurrentDateTimeInternal()
                .map(currentTime -> currentTime.compareTo(newSystemTime) == 0)
                .orElse(false);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TimeService{");
        sb.append("repository=").append("SystemTimeRepository");
        sb.append(", applicationEventPublisher=").append("applicationEventPublisher");
        sb.append('}');
        return sb.toString();
    }
}
