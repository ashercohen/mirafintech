package com.mirafintech.prototype.model.consumer.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumerEventLoanAdded extends ConsumerEvent {

    // uni-directional many-to-one:  ConsumerEventLoanAdded n --> 1 ConsumerEvent
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "loan_fk")
    @JsonIgnore
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
        super(timestamp, consumer, cause);
        this.loan = loan;
    }

    @Override
    public void handle() {
        this.consumerBalanceBefore = this.consumer.getBalance();
        this.consumerBalanceAfter = this.consumerBalanceBefore.add(this.loan.getAmount());
        this.consumer.setBalance(this.consumerBalanceAfter);
    }
}
