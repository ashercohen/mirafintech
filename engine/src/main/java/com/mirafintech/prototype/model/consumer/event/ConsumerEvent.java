package com.mirafintech.prototype.model.consumer.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.OneToManyEntityAssociation;
import com.mirafintech.prototype.model.consumer.Consumer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "CONSUMER_EVENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ConsumerEvent implements OneToManyEntityAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected LocalDateTime timestamp;

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    protected Consumer consumer;

    protected String cause; // origin/trigger // TODO: the originator of the event, "who did it, when, why"

    protected ConsumerEvent() {
    }

    private ConsumerEvent(Long id, LocalDateTime timestamp, Consumer consumer, String cause) {
        this.id = id;
        this.timestamp = timestamp;
        this.consumer = consumer;
        this.cause = cause;
    }

    protected ConsumerEvent(LocalDateTime timestamp, Consumer consumer, String cause) {
        this(null, timestamp, consumer, cause);
    }

    // TODO: maybe make protected - probably move from here as this is a model
    public abstract void handle();

    public String getType() {
        return this.getClass().getSimpleName();
    }
}
