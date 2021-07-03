package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.model.tranche.Tranche;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public final class PrinciplePaymentAllocation extends LoanPaymentAllocation {

    @Column(name = "principle__amount", precision = 13, scale = 5)
    private BigDecimal amount;

    @Column(name = "principle__loan_balance", precision = 13, scale = 5)
    private BigDecimal loanPrincipleBalance; // this value is informational - not needed in order to update the loan balance

    @Column(name = "principle__inside_grace_period", precision = 13, scale = 5)
    private boolean insideGracePeriod;

    @Column(name = "principle__grace_period_start", precision = 13, scale = 5)
    private LocalDateTime gracePeriodStart;

    protected PrinciplePaymentAllocation() {
    }

    private PrinciplePaymentAllocation(Long id,
                                       LocalDateTime timestamp,
                                       Payment payment,
                                       Loan loan,
                                       Tranche tranche,
                                       BigDecimal amount,
                                       BigDecimal loanPrincipleBalance,
                                       boolean insideGracePeriod,
                                       LocalDateTime gracePeriodStart) {
        super(id, timestamp, payment, loan, tranche);
        this.amount = amount;
        this.loanPrincipleBalance = loanPrincipleBalance;
        this.insideGracePeriod = insideGracePeriod;
        this.gracePeriodStart = gracePeriodStart;
    }

    public PrinciplePaymentAllocation(LocalDateTime timestamp,
                                      Loan loan,
                                      BigDecimal amount,
                                      BigDecimal loanPrincipleBalance,
                                      boolean insideGracePeriod,
                                      LocalDateTime gracePeriodStart) {
        this(null, timestamp, null, loan, null, amount, loanPrincipleBalance, insideGracePeriod, gracePeriodStart);
    }
}
