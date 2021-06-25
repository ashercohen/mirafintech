package com.mirafintech.prototype.model.payment.allocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.charge.InterestCharge;
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
public final class InterestPaymentAllocation extends LoanPaymentAllocation {

    // n <--> 1 bi-di "child" side
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @Setter
    @JsonIgnore
    private InterestCharge interestCharge;

    @Column(name = "interest_tranche")
    private BigDecimal trancheInterest;

    @Column(name = "interest_mira")
    private BigDecimal miraInterest;

    protected InterestPaymentAllocation() {
    }

    private InterestPaymentAllocation(Long id,
                                      LocalDateTime timestamp,
                                      Payment payment,
                                      Loan loan,
                                      Tranche tranche,
                                      InterestCharge interestCharge,
                                      BigDecimal trancheInterest,
                                      BigDecimal miraInterest) {
        super(id, timestamp, payment, loan, tranche);
        this.interestCharge = interestCharge;
        this.trancheInterest = trancheInterest;
        this.miraInterest = miraInterest;
    }

    public InterestPaymentAllocation(LocalDateTime timestamp, Loan loan, InterestCharge interestCharge, BigDecimal trancheInterest, BigDecimal miraInterest) {
        this(null, timestamp, null, loan, null, interestCharge, trancheInterest, miraInterest);
    }
}
