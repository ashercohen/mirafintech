package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "TRANCHE_EVENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class TrancheEvent extends EntityBase<TrancheEvent> {

    /**
     *  this is a base class for an hierarchy of tranche events.
     *  different events operates differently on the tranche and
     *  contain different data.
     *  for example:
     *  - loan added/removed affects balance
     *  - payment_received: balance, create additional charges
     *  - missed payment: charges,
     *  - etc...
     */

    // TODO: create more subclasses

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected LocalDateTime timestamp;

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    protected Tranche tranche;

    protected String cause; // origin/trigger // TODO: the originator of the event, "who did it, when, why"

    @Enumerated(EnumType.STRING)
    protected TrancheEventType type;

    protected TrancheEvent() {
    }

    private TrancheEvent(Long id, LocalDateTime timestamp, Tranche tranche, String cause, TrancheEventType type) {
        this.id = id;
        this.timestamp = timestamp;
        this.tranche = tranche;
        this.cause = cause;
        this.type = type;
    }

    protected TrancheEvent(LocalDateTime timestamp, Tranche tranche, String cause, TrancheEventType type) {
        this(null, timestamp, tranche, cause, type);
    }

    // TODO: maybe make protected - probably move from here as this is a model
    public abstract void handle();
}
