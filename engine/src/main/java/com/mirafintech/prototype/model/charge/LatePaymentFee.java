package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.allocation.LatePaymentFeePaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.mirafintech.prototype.model.AssociationHelper.addToCollection;
import static com.mirafintech.prototype.model.AssociationHelper.createIfNull;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public final class LatePaymentFee extends ConsumerCharge<LatePaymentFeePaymentAllocation> {

    @Column(name = "late_payment_fee__amount", precision = 13, scale = 5)
    private BigDecimal amount;

    // 1 <--> n bi-di "parent" side
    @OneToMany(mappedBy = "latePaymentFee", cascade = CascadeType.ALL, orphanRemoval = false)
    @Getter(AccessLevel.PRIVATE)
    @JsonIgnore
    private List<LatePaymentFeePaymentAllocation> latePaymentFeePaymentAllocations;

    protected LatePaymentFee() {
    }

    private LatePaymentFee(Long id,
                           LocalDateTime timestamp,
                           Consumer consumer,
                           ChargeStatus status,
                           BigDecimal amount,
                           List<LatePaymentFeePaymentAllocation> latePaymentFeePaymentAllocations) {
        super(id, timestamp, consumer, status);
        this.amount = amount;
        this.latePaymentFeePaymentAllocations = createIfNull(latePaymentFeePaymentAllocations);
    }

    public LatePaymentFee(LocalDateTime timestamp, Consumer consumer, BigDecimal amount) {
        this(null, timestamp, consumer, ChargeStatus.NOT_PAID, amount, null);
    }

    public BigDecimal balance() {

        return this.amount.subtract(
                // already paid
                this.latePaymentFeePaymentAllocations
                        .stream()
                        .map(LatePaymentFeePaymentAllocation::getAmount)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );
    }

    @Override
    public boolean addPaymentAllocation(LatePaymentFeePaymentAllocation paymentAllocation) {
        return addToCollection(this.latePaymentFeePaymentAllocations, paymentAllocation, this, "paymentAllocation", paymentAllocation::setLatePaymentFee);
    }

    public List<Long> getInterestPaymentAllocationsIds() {
        return this.latePaymentFeePaymentAllocations.stream().map(PaymentAllocation::getId).toList();
    }
}
