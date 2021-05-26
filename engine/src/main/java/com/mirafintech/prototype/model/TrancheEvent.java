package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "TRANCHE_EVENT")
@Getter
@Setter
//@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrancheEvent extends EntityBase<TrancheEvent> {

    /**
     * TODO:
     *  we might need to create an hierarchy of events since each events operates differently on
     *  the tranche and contains different data. for example:
     *  - loan added/removed affects balance
     *  - payment_received: balance, create additional charges
     *  - missed payment: charges,
     *  - etc...
     *  we might be able to make a "TrancheEventMetadata" table/entity that contains all the a.m. data that only
     *  relevant fields will be populated. we can also add this here
     */

    public enum Type {
        LOAN_ADDED, LOAN_REMOVED, PAYMENT_RECEIVED, PAYMENT_MISSED // TODO: add here more types of events/operations on tranche
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Tranche tranche;

    @Enumerated(EnumType.STRING)
    private Type type;

    private TrancheEvent(Long id, LocalDateTime timestamp, Tranche tranche, Type type) {
        this.id = id;
        this.timestamp = timestamp;
        this.tranche = tranche;
        this.type = type;
    }

    public TrancheEvent(LocalDateTime timestamp, Tranche tranche, Type type) {
        this(null, timestamp, tranche, type);
    }
}
