package com.mirafintech.prototype.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;


public class EndOfDayEvent extends ApplicationEvent {

    private final LocalDate dayEnded;

    public EndOfDayEvent(Object source, LocalDate dayEnded) {
        super(source);
        this.dayEnded = dayEnded;
    }

    public LocalDate getDayEnded() {
        return this.dayEnded;
    }

    @Override
    public String toString() {

        return new StringBuffer(this.getClass().getSimpleName())
                .append("{")
                .append("dayEnded=")
                .append(dayEnded)
                .append('}')
                .toString();
    }
}
