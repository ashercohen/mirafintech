package com.mirafintech.prototype.model.loan.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "LOAN_EVENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class LoanEvent {

    /**
     *  this is a base class for an hierarchy of loan events.
     *  different events operates differently on the loan and
     *  contain different data.
     *  for example:
     *  - loan added/removed affects balance
     *  - payment_received: balance, create additional charges
     *  - missed payment: charges,
     *  - etc...
     */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @Setter
    @JsonIgnore
    protected Loan loan;

    protected String cause; // origin/trigger // TODO: the originator of the event, "who did it, when, why"

    protected LoanEvent() {
    }

    private LoanEvent(Long id, LocalDateTime timestamp, Loan loan, String cause) {
        this.id = id;
        this.timestamp = timestamp;
        this.loan = loan;
        this.cause = cause;
    }

    protected LoanEvent(LocalDateTime timestamp, Loan loan, String cause) {
        this(null, timestamp, loan, cause);
    }

    // TODO: maybe make protected - probably move from here as this is a model
    public abstract void handle();

    public String getType() {
        return this.getClass().getSimpleName();
    }
}
