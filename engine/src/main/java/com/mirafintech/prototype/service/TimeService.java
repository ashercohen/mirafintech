package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.repository.SystemTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class TimeService {

    @Autowired
    private SystemTimeRepository repository;

    public SystemTime setTime(LocalDateTime dateTime) {
        SystemTime systemTime = new SystemTime(dateTime);
        return repository.saveAndFlush(systemTime);
    }

    public SystemTime getCurrentSystemTime() {
        return this.repository.findTopByOrderByIdDesc();
    }

    public LocalDateTime getCurrentTime() {
        return this.repository.findTopByOrderByIdDesc().getDateTime();
    }
}
