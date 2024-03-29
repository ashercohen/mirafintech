package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.charge.LatePaymentFee;
import com.mirafintech.prototype.model.charge.LoanFee;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.model.tranche.Tranche;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public final class LoanFeePaymentAllocation extends LoanPaymentAllocation {

    // n <--> 1 bi-di "child" side
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @Setter
    @JsonIgnore
    private LoanFee loanFee;

    @Column(name = "loan_fee_amount", precision = 16, scale = 5)
    private BigDecimal amount;

    protected LoanFeePaymentAllocation() {
    }

    private LoanFeePaymentAllocation(Long id, LocalDateTime timestamp, Payment payment, Loan loan, Tranche tranche, LoanFee loanFee, BigDecimal amount) {
        super(id, timestamp, payment, loan, tranche);
        this.loanFee = loanFee;
        this.amount = amount;
    }

    public LoanFeePaymentAllocation(LocalDateTime timestamp, Loan loan, LoanFee loanFee, BigDecimal amount) {
        this(null, timestamp, null, loan, null, loanFee, amount);
    }
}
