package com.mirafintech.prototype.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

import static com.mirafintech.prototype.model.AssociationHelper.addToCollection;


@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long externalId;

    private LocalDateTime timestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Consumer consumer;

    @Column(precision = 16, scale = 5)
    private BigDecimal amount;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();

    public static Payment create(Long externalId,
                                 LocalDateTime timestamp,
                                 Consumer consumer,
                                 BigDecimal amount,
                                 List<PaymentAllocation> paymentAllocations) {

        if (consumer == null) throw new RuntimeException("consumer is null");

        Payment payment = new Payment(null, externalId, timestamp, consumer, amount);
        paymentAllocations.forEach(payment::addPaymentAllocation);

        return payment;
    }

    private Payment(Long id, Long externalId, LocalDateTime timestamp, Consumer consumer, BigDecimal amount) {
        this.id = id;
        this.externalId = externalId;
        this.timestamp = timestamp;
        this.consumer = consumer;
        this.amount = amount;
    }

    public boolean addPaymentAllocation(PaymentAllocation paymentAllocation) {
        return addToCollection(this.paymentAllocations, paymentAllocation, this, "paymentAllocation", paymentAllocation::setPayment);
    }

    public PaymentDetails details() {

        return new PaymentDetails(
                this.id,
                this.externalId,
                this.timestamp,
                this.consumer.getId(),
                this.amount,
                this.paymentAllocations.stream().map(pa -> new AllocationDetails(pa.getId(), pa.getType())).toList()
        );
    }
}
