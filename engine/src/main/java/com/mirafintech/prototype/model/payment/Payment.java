package com.mirafintech.prototype.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.OneToManyEntityAssociation;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment implements OneToManyEntityAssociation {

    @Id
    private Long id;

    private LocalDateTime timestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Consumer consumer;

    private BigDecimal amount;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();

    public static Payment create(Long id,
                                 LocalDateTime timestamp,
                                 Consumer consumer,
                                 BigDecimal amount,
                                 List<PaymentAllocation> paymentAllocations) {
        Payment payment = new Payment(id, timestamp, consumer, amount);
        paymentAllocations.forEach(payment::addPaymentAllocation);

        return payment;
    }

    private Payment(Long id, LocalDateTime timestamp, Consumer consumer, BigDecimal amount) {
        this.id = id;
        this.timestamp = timestamp;
        this.consumer = consumer;
        this.amount = amount;
    }

    public boolean addPaymentAllocation(PaymentAllocation paymentAllocation) {
        return addToCollection(this.paymentAllocations, paymentAllocation, this, "paymentAllocation", paymentAllocation::setPayment);
    }
}
