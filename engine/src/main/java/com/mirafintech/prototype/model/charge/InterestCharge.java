package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.allocation.InterestPaymentAllocation;
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


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class InterestCharge extends LoanCharge<InterestPaymentAllocation> {

    @Column(name = "interest_charge__amount")
    @Getter(AccessLevel.PROTECTED)
    private BigDecimal amount;

    // 1 <--> n bi-di "parent" side
    @OneToMany(mappedBy = "interestCharge", cascade = CascadeType.ALL, orphanRemoval = false)
    @Getter(AccessLevel.PRIVATE)
    @JsonIgnore
    private List<InterestPaymentAllocation> interestPaymentAllocations;

    protected InterestCharge() {
    }

    private InterestCharge(Long id,
                           LocalDateTime timestamp,
                           Loan loan,
                           ChargeStatus status,
                           BigDecimal amount,
                           List<InterestPaymentAllocation> interestPaymentAllocations) {
        super(id, timestamp, loan, status);
        this.amount = amount;
        this.interestPaymentAllocations = createIfNull(interestPaymentAllocations);
    }

    public InterestCharge(LocalDateTime timestamp, Loan loan, BigDecimal amount) {
        this(null, timestamp, loan, ChargeStatus.NOT_PAID, amount, null);
    }

    public BigDecimal balance() {

        return this.amount.subtract(
                // already paid
                this.interestPaymentAllocations
                        .stream()
                        .map(allocation -> allocation.getTrancheInterest().add(allocation.getMiraInterest()))
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );
    }

    @Override
    public boolean addPaymentAllocation(InterestPaymentAllocation paymentAllocation) {
        return addToCollection(this.interestPaymentAllocations, paymentAllocation,  this,"paymentAllocation", paymentAllocation::setInterestCharge);
    }

    public List<Long> getInterestPaymentAllocationsIds() {
        return this.interestPaymentAllocations.stream().map(PaymentAllocation::getId).toList();
    }
}
