package com.mirafintech.prototype.model.interest;

import java.time.LocalDate;


public record RawInterval(LocalDate from, LocalDate to) implements Interval<Void> {

    public RawInterval {
        validate(from, to);
    }

    @Override
    public Void value() {
        return null;
    }

//    /**
//     * return true if this interval is contained in (or equals to) other:
//     * this.from >= other.from
//     * this.to <= other.to
//     */
//    public boolean isContainedIn(RawInterval other) {
//        return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
//    }


    /**
     * return true if this raw interval and other interval have date(s) intersection
     *
     */
    public boolean intersects(Interval<?> other) {
        return this.to.isAfter(other.from()) && other.to().isAfter(this.from);
    }
}
