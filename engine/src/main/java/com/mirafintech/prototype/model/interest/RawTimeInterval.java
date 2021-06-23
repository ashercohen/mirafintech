package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;


public record RawTimeInterval(LocalDate from, LocalDate to) implements TimeInterval<Void> {

    public RawTimeInterval {
        validate(from, to);
    }

    @Override
    public Void value() {
        return null;
    }

    /**
     * return true if this interval is contained in (or equals to) other:
     * this.from >= other.from
     * this.to <= other.to
     */
    public boolean isContainedIn(com.mirafintech.prototype.model.interest.RawTimeInterval other) {
        return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
    }
}
