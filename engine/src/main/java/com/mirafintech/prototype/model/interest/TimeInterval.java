package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;


public interface TimeInterval<V> {

    LocalDate from();
    LocalDate to();
    V value();

    default void validate(LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException(String.format("illegal interval: from=%s, to=%s", from, to));
        }
    }

    /**
     * creates an anonymous object of type TimeInterval from a and object of class that implements TimeInterval interface
     * used to merge and sort different classes that implement TimeInterval interface
     */
    static TimeInterval<?> toTimeInterval(TimeInterval<?> interval) { //TODO: delete if possible, fix type if not
        return new TimeInterval<Void>() {
            @Override
            public LocalDate from() {
                return interval.from();
            }

            @Override
            public LocalDate to() {
                return interval.to();
            }

            @Override
            public Void value() {
                return null;
            }
        };
    }
}
