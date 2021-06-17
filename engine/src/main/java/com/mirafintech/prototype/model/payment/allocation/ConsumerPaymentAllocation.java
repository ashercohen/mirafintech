package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Payee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.model.tranche.Tranche;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;


@Entity
//@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class ConsumerPaymentAllocation extends PaymentAllocation {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JsonIgnore
    protected Consumer consumer;

    protected ConsumerPaymentAllocation() {
    }

    protected ConsumerPaymentAllocation(Long id, LocalDateTime timestamp, Payment payment, Consumer consumer) {
        super(id, timestamp, payment);
        this.consumer = consumer;
    }

    @Override
    public Payee getPayee() {
        return this.consumer;
    }
}
