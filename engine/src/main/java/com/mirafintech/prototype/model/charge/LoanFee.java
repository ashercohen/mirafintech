package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.allocation.LoanFeePaymentAllocation;
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
public final class LoanFee extends LoanCharge<LoanFeePaymentAllocation> {

    @Column(name = "loan_fee__amount")
    @Getter(AccessLevel.PROTECTED)
    private BigDecimal amount;

    // 1 <--> n bi-di "parent" side
    @OneToMany(mappedBy = "loanFee", cascade = CascadeType.ALL, orphanRemoval = false)
    @Getter(AccessLevel.PRIVATE)
    private List<LoanFeePaymentAllocation> loanFeePaymentAllocations;

    protected LoanFee() {
    }

    private LoanFee(Long id,
                    LocalDateTime timestamp,
                    Loan loan,
                    ChargeStatus status,
                    BigDecimal amount,
                    List<LoanFeePaymentAllocation> loanFeePaymentAllocations) {
        super(id, timestamp, loan, status);
        this.amount = amount;
        this.loanFeePaymentAllocations = createIfNull(loanFeePaymentAllocations);
    }

    public LoanFee(LocalDateTime timestamp, Loan loan, BigDecimal amount) {
        this(null, timestamp, loan, ChargeStatus.NOT_PAID, amount, null);
    }

    public BigDecimal balance() {

        return this.amount.subtract(
                // already paid
                this.loanFeePaymentAllocations
                        .stream()
                        .map(LoanFeePaymentAllocation::getAmount)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );
    }

    @Override
    public boolean addPaymentAllocation(LoanFeePaymentAllocation paymentAllocation) {
        return addToCollection(this.loanFeePaymentAllocations, paymentAllocation, this, "paymentAllocation", paymentAllocation::setLoanFee);
    }

    /**
     * use by jackson when serializing this object: use ids only instead of having cycles
     */
    public List<Long> getLoanFeePaymentAllocationsIds() {
        return this.loanFeePaymentAllocations.stream().map(PaymentAllocation::getId).toList();
    }
}
