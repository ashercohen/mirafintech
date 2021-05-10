package com.mirafintech.prototype.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;


public class NewDateEvent extends ApplicationEvent {

    private final LocalDateTime newDate;

    public NewDateEvent(Object source, LocalDateTime newDate) {
        super(source);
        this.newDate = newDate;
    }

    public LocalDateTime getNewDate() {
        return this.newDate;
    }
}
