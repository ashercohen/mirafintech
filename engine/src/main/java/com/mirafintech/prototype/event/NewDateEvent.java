package com.mirafintech.prototype.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;


public class NewDateEvent extends ApplicationEvent {

    private final LocalDateTime dateTime;

    public NewDateEvent(Object source, LocalDateTime dateTime) {
        super(source);
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NewDateEvent{");
        sb.append("dateTime=").append(dateTime);
        sb.append('}');
        return sb.toString();
    }
}
