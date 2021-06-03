package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumerEventLoanAdded extends ConsumerEvent {

    // when serializing to json the loan appears twice: here and in loans list of the consumer
    // TODO: check if optional=false work with inheritance hierarchy (we're using SINGLE_TABLE @nonNull isn't supported)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "tranche_event_fk") // TODO: maybe need to rename to "loan_added__tranche_event_fk"
    private Loan loan;

    @Column(name = "loan_added__balance_before")
    private BigDecimal consumerBalanceBefore;

    @Column(name = "loan_added__balance_after")
    private BigDecimal consumerBalanceAfter;


    public static ConsumerEventLoanAdded create(Loan loan, Consumer consumer, LocalDateTime timestamp, String cause) {

        ConsumerEventLoanAdded event = new ConsumerEventLoanAdded(loan, consumer, timestamp, cause);
        consumer.addConsumerEvent(event);
        event.handle();

        return event;
    }

    protected ConsumerEventLoanAdded() {
    }

    private ConsumerEventLoanAdded(Loan loan, Consumer consumer, LocalDateTime timestamp, String cause) {
        super(timestamp, consumer, cause, ConsumerEventType.LOAN_ADDED);
        this.loan = loan;
    }

    @Override
    public void handle() {
        this.consumerBalanceBefore = this.consumer.getCurrentBalance();
        this.consumerBalanceAfter = this.consumerBalanceBefore.add(this.loan.getAmount());
        this.consumer.setCurrentBalance(this.consumerBalanceAfter);
    }
}
