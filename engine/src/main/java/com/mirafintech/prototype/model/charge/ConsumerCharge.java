package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.allocation.ConsumerPaymentAllocation;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mirafintech.prototype.model.charge.ChargeStatus.*;


@Entity
//@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class ConsumerCharge<P extends ConsumerPaymentAllocation> extends Charge {

    public abstract boolean addPaymentAllocation(P paymentAllocation);

    public abstract BigDecimal balance();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JsonIgnore
    protected Consumer consumer;

    protected ConsumerCharge() {
    }

    protected ConsumerCharge(Long id, LocalDateTime timestamp, Consumer consumer, ChargeStatus status) {
        super(id, timestamp, status);
        this.consumer = consumer;
    }

    @Override
    protected void verifyApplicableForChargeType(ChargeStatus status) {
        if (status == MOVED_TO_LOAN_BALANCE || status == DEDUCTED_FROM_MERCHANT_PAYMENT) {
            throw new IllegalArgumentException("illegal consumer charge status: " + status);
        }
    }

    @Override
    public boolean isPending() {
        /**
         * enough to check for NOT_PAID:
         * - 2 statuses are illegal - see verifyStatus()
         * - 3 don't require payment
         */
        return this.status == NOT_PAID;
    }
}
