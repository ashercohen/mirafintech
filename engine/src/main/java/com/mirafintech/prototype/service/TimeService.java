package com.mirafintech.prototype.service;

import com.mirafintech.prototype.event.NewDateEvent;
import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.repository.SystemTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Service
public class TimeService {

    @Autowired
    private SystemTimeRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public SystemTime setTime(LocalDateTime newDateTime) {
        SystemTime systemTime = new SystemTime(newDateTime);

        LocalDateTime currentTime = getCurrentDateTime();

        // TODO: test this:
        //  - check inclusive/exclusive
        //  - who persists newDateTime? eventHandler? this? what about the other days in this stream
        Stream.iterate(currentTime, date -> date.plusDays(1L))
               .takeWhile(date -> date.isBefore(newDateTime))
               .map(date -> new NewDateEvent(this, date))
               .forEach(applicationEventPublisher::publishEvent);

        // TODO: should this be persisted here or in event handler?
        return repository.saveAndFlush(systemTime);
    }

    public SystemTime getCurrentSystemTime() {
        return this.repository.findTopByOrderByIdDesc();
    }

    public LocalDateTime getCurrentDateTime() {
        return this.repository.findTopByOrderByIdDesc().getDateTime();
    }
}
