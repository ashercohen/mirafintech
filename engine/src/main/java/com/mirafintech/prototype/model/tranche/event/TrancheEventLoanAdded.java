package com.mirafintech.prototype.model.tranche.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.tranche.Tranche;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class TrancheEventLoanAdded extends TrancheEvent {

    // uni-directional many-to-one:  TrancheEventLoanAdded n --> 1 Loan
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "loan_fk")
    private Loan loan;

    @Column(name = "loan_added__balance_before")
    private BigDecimal trancheBalanceBefore;

    @Column(name = "loan_added__balance_after")
    private BigDecimal trancheBalanceAfter;

    /**
     * create and handle/apply event on tranche
     */
    public static TrancheEventLoanAdded create(Loan loan, Tranche tranche, LocalDateTime timestamp, String cause) {

        TrancheEventLoanAdded event = new TrancheEventLoanAdded(loan, tranche, timestamp, cause);
        tranche.addEventEvent(event);
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
