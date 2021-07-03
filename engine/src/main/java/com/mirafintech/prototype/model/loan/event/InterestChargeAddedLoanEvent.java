package com.mirafintech.prototype.model.loan.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.loan.Loan;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class InterestChargeAddedLoanEvent extends LoanEvent {

    // uni-directional many-to-one:  LoanEventInterestChargeAdded n --> 1 LoanEvent
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "interest_charge_fk")
    @JsonIgnore
    private InterestCharge interestCharge; // TODO: maybe 1:1

    @Column(name = "interest_added__amount", precision = 13, scale = 5)
    private BigDecimal amount;

    @Column(name = "interest_added__balance_after", precision = 13, scale = 5)
    private BigDecimal loanBalanceBefore;

    protected InterestChargeAddedLoanEvent() {
        // make spring happy
    }

    public InterestChargeAddedLoanEvent(LocalDateTime timestamp,
                                        Loan loan,
                                        String cause,
                                        InterestCharge interestCharge) {
        super(timestamp, loan, cause/*, LoanEventType.INTEREST_CHARGE_ADDED*/);
        this.interestCharge = interestCharge;
        this.amount = interestCharge.balance();
        this.loanBalanceBefore = loan.currentBalance();
    }

    @Override
    public void handle() {
        // nothing to do - this is informational event
    }
}
