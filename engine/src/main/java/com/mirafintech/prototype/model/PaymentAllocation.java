package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "PAYMENT_ALLOCATION")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAllocation extends EntityBase<PaymentAllocation> {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Tranche tranche;

    private BigDecimal principle;

    private BigDecimal interest;

    private PaymentAllocation(Long id, Payment payment, Loan loan, Tranche tranche, BigDecimal principle, BigDecimal interest) {
        this.id = id;
        this.payment = payment;
        this.loan = loan;
        this.tranche = tranche;
        this.principle = principle;
        this.interest = interest;
    }
}
