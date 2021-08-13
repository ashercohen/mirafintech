package com.mirafintech.prototype.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.HasAmount;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.allocation.PrinciplePaymentAllocation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;


/**
 * this is a request - not a charge.
 * it is used to determine/explain the principle component of a MinimumPaymentConsumerEvent
 * it is up to the PaymentAllocationPolicy to allocate money to a loan.
 */
@Entity
@Table(name = "PRINCIPLE_MINIMUM_PAYMENT")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrincipleMinimumPayment implements HasAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
//    @OneToOne(fetch = FetchType.LAZY, optional = false, orphanRemoval = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "loan_fk")
    private Loan loan;

    @Column(precision = 16, scale = 5)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY, optional = true, orphanRemoval = false)
    @JoinColumn(name = "principle_ayment_llocation_fk")
    private PrinciplePaymentAllocation principlePaymentAllocation;

    protected PrincipleMinimumPayment() {
    }

    private PrincipleMinimumPayment(Long id, Loan loan, BigDecimal amount, PrinciplePaymentAllocation principlePaymentAllocation) {
        this.id = id;
        this.loan = loan;
        this.amount = amount;
        this.principlePaymentAllocation = principlePaymentAllocation;
    }

    // TODO: remove if not used
    public PrincipleMinimumPayment(Loan loan, BigDecimal amount, PrinciplePaymentAllocation principlePaymentAllocation) {
        this(null, loan, amount, principlePaymentAllocation);
    }

    public PrincipleMinimumPayment(Loan loan, BigDecimal amount) {
        this(null, loan, amount, null);
    }
}
