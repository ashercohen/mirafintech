package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.charge.LatePaymentFee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.Payment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public final class LatePaymentFeePaymentAllocation extends ConsumerPaymentAllocation {

    // n <--> 1 bi-di "child" side
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JsonIgnore
    @Setter
    private LatePaymentFee latePaymentFee;

    @Column(name = "late_payment_fee_amount", precision = 16, scale = 5)
    private BigDecimal amount;

    protected LatePaymentFeePaymentAllocation() {
    }

    public LatePaymentFeePaymentAllocation(Long id, LocalDateTime timestamp, Payment payment, Consumer consumer, LatePaymentFee latePaymentFee, BigDecimal amount) {
        super(id, timestamp, payment, consumer);
        this.latePaymentFee = latePaymentFee;
        this.amount = amount;
    }

    public LatePaymentFeePaymentAllocation(LocalDateTime timestamp, Consumer consumer, LatePaymentFee latePaymentFee, BigDecimal amount) {
        this(null, timestamp, null, consumer, latePaymentFee, amount);
    }

}
