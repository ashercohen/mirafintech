package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Payee;
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
public abstract /*sealed*/ class LoanPaymentAllocation extends PaymentAllocation
        /*permits InterestPaymentAllocation, PrinciplePaymentAllocation, LoanFeePaymentAllocation*/ {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = true)
    @JsonIgnore
    protected Loan loan;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    protected Tranche tranche;

    protected LoanPaymentAllocation() {
    }

    public LoanPaymentAllocation(Long id, LocalDateTime timestamp, Payment payment, Loan loan, Tranche tranche) {
        super(id, timestamp, payment);
        this.loan = loan;
        this.tranche = tranche;
    }

    @Override
    public Payee getPayee() {
        return this.loan;
    }
}
