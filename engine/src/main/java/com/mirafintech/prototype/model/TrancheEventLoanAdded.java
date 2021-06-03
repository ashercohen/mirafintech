package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrancheEventLoanAdded extends TrancheEvent {

    // TODO: check if optional=false work with inheritance hierarchy (we're using SINGLE_TABLE @nonNull isn't supported)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "tranche_event_fk") // TODO: maybe need to rename to "loan_added__tranche_event_fk"
    private Loan loan;

    @Column(name = "loan_added__balance_before")
    private BigDecimal trancheBalanceBefore;

    @Column(name = "loan_added__balance_after")
    private BigDecimal trancheBalanceAfter;

    /**
     * create and handle/apply event on tranche
     */
    public static TrancheEventLoanAdded createTrancheEventLoanAdded(Loan loan, Tranche tranche, LocalDateTime timestamp, String cause) {

        TrancheEventLoanAdded event = new TrancheEventLoanAdded(loan, tranche, timestamp, cause);
        tranche.addTrancheEvent(event);
        event.handle();

        return event;
    }

    protected TrancheEventLoanAdded() {
        // make spring happy
    }

    private TrancheEventLoanAdded(Loan loan, Tranche tranche, LocalDateTime timestamp, String cause) {
        super(timestamp, tranche, cause, TrancheEventType.LOAN_ADDED);
        this.loan = loan;
    }

    @Override
    public void handle() {
        this.trancheBalanceBefore = this.tranche.currentBalance();
        this.trancheBalanceAfter = this.trancheBalanceBefore.subtract(this.loan.getAmount());
        this.tranche.setCurrentBalance(this.trancheBalanceAfter);
    }
}
