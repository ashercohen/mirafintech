package com.mirafintech.prototype.model.loan.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.charge.ChargeStatus;
import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.charge.LoanFee;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.allocation.InterestPaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.LoanFeePaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.LoanPaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.PrinciplePaymentAllocation;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class PaymentAllocationAddedLoanEvent extends LoanEvent {
    /**
     * TODO: this event can be sub-classes into 3 (same as PA subclassing) and simplify the handle() method
     */

    // uni-directional many-to-one:  PaymentAllocationAddedLoanEvent n --> 1 LoanEvent
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "loan_payment_allocation_fk")
    @JsonIgnore
    private LoanPaymentAllocation paymentAllocation; // TODO: maybe 1:1

    @Column(name = "allocation_added__loan_balance_before", precision = 13, scale = 5)
    private BigDecimal loanBalanceBefore;

    @Column(name = "allocation_added__loan_balance_after", precision = 13, scale = 5)
    private BigDecimal loanBalanceAfter;

    @Column(name = "allocation_added__fee_balance_before", precision = 13, scale = 5)
    private BigDecimal feeBalanceBefore;

    @Column(name = "allocation_added__fee_balance_after", precision = 13, scale = 5)
    private BigDecimal feeBalanceAfter;

    @Column(name = "allocation_added__interest_balance_before", precision = 13, scale = 5)
    private BigDecimal interestBalanceBefore;

    @Column(name = "allocation_added__interest_balance_after", precision = 13, scale = 5)
    private BigDecimal interestBalanceAfter;

    protected PaymentAllocationAddedLoanEvent() {
        // make spring happy
    }

    public PaymentAllocationAddedLoanEvent(LoanPaymentAllocation loanPaymentAllocation, Loan loan, LocalDateTime timestamp, String cause) {
        super(timestamp, loan, cause/*, LoanEventType.PAYMENT_ALLOCATION_ADDED*/);
        this.paymentAllocation = loanPaymentAllocation;
    }

    @Override
    public void handle() {

        if (this.paymentAllocation instanceof LoanFeePaymentAllocation loanFeeAllocation) {
            handle(loanFeeAllocation);
        } else if (paymentAllocation instanceof InterestPaymentAllocation interestPaymentAllocation) {
            handle(interestPaymentAllocation);
        } else if (paymentAllocation instanceof PrinciplePaymentAllocation principlePaymentAllocation) {
            handle(principlePaymentAllocation);
        } else {
            throw new RuntimeException("unexpected LoanPaymentAllocation sub-type: " + this.paymentAllocation.getClass().getSimpleName());
        }
    }

    private void handle(LoanFeePaymentAllocation loanFeeAllocation) {

        LoanFee loanFee = loanFeeAllocation.getLoanFee();
        BigDecimal amount = loanFeeAllocation.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("non-positive payment allocation amount: " + amount);
        }

        this.feeBalanceBefore = loanFee.balance();

        if (amount.compareTo(this.feeBalanceBefore) >= 0) {
            // paid in full
            loanFee.setStatus(ChargeStatus.PAID);
            this.feeBalanceAfter = BigDecimal.ZERO;
        } else {
            // partial payment
            loanFee.setStatus(ChargeStatus.PARTIALLY_PAID);
            this.feeBalanceAfter = feeBalanceBefore.subtract(amount);
        }

        loanFee.addPaymentAllocation(loanFeeAllocation);

        // sanity
        if (this.feeBalanceBefore.subtract(amount).compareTo(loanFee.balance()) != 0) {
            throw new RuntimeException(
                    String.format("unexpected loan fee balance: val1=%s val2=%s",
                            this.feeBalanceBefore.subtract(amount), loanFee.balance()));
        }
    }

    private void handle(InterestPaymentAllocation interestPaymentAllocation) {

        // charge value
        InterestCharge interestCharge = interestPaymentAllocation.getInterestCharge();
        // allocations
        BigDecimal miraInterestAllocation = interestPaymentAllocation.getMiraInterest();
        BigDecimal trancheInterestAllocation = interestPaymentAllocation.getTrancheInterest();

        if (miraInterestAllocation.compareTo(BigDecimal.ZERO) <= 0 && trancheInterestAllocation.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    String.format("non-positive payment allocations amounts: miraInterestAllocation=%s, trancheInterestAllocation=%s",
                            miraInterestAllocation, trancheInterestAllocation));
        }

        this.interestBalanceBefore = interestCharge.balance();
        BigDecimal amount = miraInterestAllocation.add(trancheInterestAllocation);

        if (amount.compareTo(this.interestBalanceBefore) >= 0) {
            // paid in full
            interestCharge.setStatus(ChargeStatus.PAID);
            this.interestBalanceAfter = BigDecimal.ZERO;
        } else {
            // partial payment
            interestCharge.setStatus(ChargeStatus.PARTIALLY_PAID);
            this.interestBalanceAfter = this.interestBalanceBefore.subtract(amount);
//                interestCharge.addPaymentAllocation(interestPaymentAllocation); moved outside the if-else
            // sanity
            if (this.interestBalanceBefore.subtract(amount).compareTo(interestCharge.balance()) != 0) {
                throw new RuntimeException(
                        String.format("unexpected interest charge balance: val1=%s val2=%s",
                                this.interestBalanceBefore.subtract(amount), interestCharge.balance()));
            }
        }

        interestCharge.addPaymentAllocation(interestPaymentAllocation);
    }

    /**
     * TODO:
     *   in this event handling, the Loan is updated.
     *   need to set rules whether events (+ event handling) do change/mutate the loan/consumer/tranche/... or
     *   just used for tracking/logging/...
     */
    private void handle(PrinciplePaymentAllocation principlePaymentAllocation) {

        this.loanBalanceBefore = this.loan.currentBalance();
        BigDecimal amount = principlePaymentAllocation.getAmount();

        LocalDateTime paymentConsiderationDate = principlePaymentAllocation.isInsideGracePeriod()
                ? principlePaymentAllocation.getGracePeriodStart()
                : paymentAllocation.getTimestamp();

        this.loanBalanceAfter = this.loan.deposit(amount, paymentConsiderationDate);
    }
}
