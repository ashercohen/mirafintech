package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.allocation.LoanPaymentAllocation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static com.mirafintech.prototype.model.charge.ChargeStatus.*;


@Entity
//@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract sealed class LoanCharge<P extends LoanPaymentAllocation> extends Charge
        permits InterestCharge, LoanFee {

    public abstract boolean addPaymentAllocation(P paymentAllocation);

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @Setter // TODO: check if required - see Loan::addLoanCharge
    @JsonIgnore
    protected Loan loan;

    protected LoanCharge() {
    }

    protected LoanCharge(Long id, LocalDateTime timestamp, Loan loan, ChargeStatus status) {
        super(id, timestamp, status);
        this.loan = loan;
    }

    @Override
    protected void verifyApplicableForChargeType(ChargeStatus status) {
        if (status == MOVED_TO_CONSUMER_BALANCE || status == DEDUCTED_FROM_MERCHANT_PAYMENT) {
            throw new IllegalArgumentException("illegal consumer charge status: " + status);
        }
    }

    @Override
    public boolean isPending() {
        return this.status == NOT_PAID;
    }
}
