package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Payee;
import com.mirafintech.prototype.model.payment.Payment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "PAYMENT_ALLOCATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
// a single allocation cannot include fee, interest, principle (mutually exclusive) => hierarchy
public /*sealed*/ abstract class PaymentAllocation
        /*permits LoanPaymentAllocation, ConsumerPaymentAllocation*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected LocalDateTime timestamp;

    // TODO: maybe change type of propagation/cascade
    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    protected Payment payment;

    @JsonIgnore
    public abstract Payee getPayee();

    @JsonIgnore
    public abstract BigDecimal getAmount();

    protected PaymentAllocation() {
    }

    protected PaymentAllocation(Long id, LocalDateTime timestamp, Payment payment) {
        this.id = id;
        this.timestamp = timestamp;
        this.payment = payment;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }
}
