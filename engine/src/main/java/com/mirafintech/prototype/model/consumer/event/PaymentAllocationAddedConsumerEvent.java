package com.mirafintech.prototype.model.consumer.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.charge.ChargeStatus;
import com.mirafintech.prototype.model.charge.LatePaymentFee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.allocation.ConsumerPaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.LatePaymentFeePaymentAllocation;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public final class PaymentAllocationAddedConsumerEvent extends ConsumerEvent { // TODO: consider removing this event

    // uni-directional many-to-one:  PaymentAllocationAddedConsumerEvent n --> 1 ConsumerEvent
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "consumer_payment_allocation_fk")
    @JsonIgnore
    private ConsumerPaymentAllocation paymentAllocation; // TODO: maybe 1:1

    @Column(name = "allocation_added__consumer_balance_before", precision = 13, scale = 5)
    private BigDecimal consumerBalanceBefore;

    @Column(name = "allocation_added__consumer_balance_after", precision = 13, scale = 5)
    private BigDecimal consumerBalanceAfter;

    @Column(name = "allocation_added__fee_balance_before", precision = 13, scale = 5)
    private BigDecimal feeBalanceBefore;

    @Column(name = "allocation_added__fee_balance_after", precision = 13, scale = 5)
    private BigDecimal feeBalanceAfter;

    protected PaymentAllocationAddedConsumerEvent() {
        // make spring happy
    }

    public PaymentAllocationAddedConsumerEvent(LocalDateTime timestamp,
                                               Consumer consumer,
                                               String cause,
                                               ConsumerPaymentAllocation paymentAllocation) {
        super(null, timestamp, consumer, cause);
        this.paymentAllocation = paymentAllocation;
    }

    @Override
    public void handle() {

        if (!(this.paymentAllocation instanceof LatePaymentFeePaymentAllocation latePaymentFeePaymentAllocation)) {
            throw new RuntimeException("unexpected ConsumerPaymentAllocation sub-type: " + this.paymentAllocation.getClass().getSimpleName());
        }

        LatePaymentFee latePaymentFee = latePaymentFeePaymentAllocation.getLatePaymentFee();
        BigDecimal amount = latePaymentFeePaymentAllocation.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("non-positive payment allocation amount: " + amount);
        }

        this.feeBalanceBefore = latePaymentFee.balance();

        if (amount.compareTo(this.feeBalanceBefore) >= 0) {
            // full payment
            latePaymentFee.setStatus(ChargeStatus.PAID);
            this.feeBalanceAfter = BigDecimal.ZERO;
        } else {

            // TODO: need to test this. sanity c/heck looks weird. manually create fee and PA smaller than fee

            // partial payment
            latePaymentFee.setStatus(ChargeStatus.PARTIALLY_PAID);
            this.feeBalanceAfter = this.feeBalanceBefore.subtract(amount);
        }

        latePaymentFee.addPaymentAllocation(latePaymentFeePaymentAllocation);

        // sanity
        if (this.feeBalanceBefore.subtract(amount).compareTo(latePaymentFee.balance()) != 0) {
            throw new RuntimeException(
                    String.format("unexpected loan fee balance: val1=%s val2=%s",
                            this.feeBalanceBefore.subtract(amount), latePaymentFee.balance()));
        }
    }
}
