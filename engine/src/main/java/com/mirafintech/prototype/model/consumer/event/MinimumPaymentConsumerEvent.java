package com.mirafintech.prototype.model.consumer.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.HasAmount;
import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.charge.LoanFee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.PrincipleMinimumPayment;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mirafintech.prototype.model.AssociationHelper.createIfNull;


@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MinimumPaymentConsumerEvent extends ConsumerEvent { // TODO: maybe make this class not a ConsumerEvent and keep it in a dedicated List at the consumer

    private LocalDate dueDate; // end of grace period

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "minimum_payment_event_fk") // references from consumer
    private List<InterestCharge> interestCharges = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "minimum_payment_event_fk") // references from consumer
    private List<LoanFee> loanFees;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "minimum_payment_event_fk") // references from consumer
    private List<PrincipleMinimumPayment> principleMinimumPayments;

    @Column(precision = 13, scale = 5)
    private BigDecimal totalInterestCharges;

    @Column(precision = 13, scale = 5)
    private BigDecimal totalFees;

    @Column(precision = 13, scale = 5)
    private BigDecimal totalPrinciple;

    @Column(precision = 13, scale = 5)
    private BigDecimal total;

    protected MinimumPaymentConsumerEvent() {
    }

    public MinimumPaymentConsumerEvent(LocalDateTime timestamp,
                                       Consumer consumer,
                                       String cause,
                                       LocalDate dueDate,
                                       List<InterestCharge> interestCharges,
                                       List<LoanFee> loanFees,
                                       List<PrincipleMinimumPayment> principleMinimumPayments) {
        super(null, timestamp, consumer, cause);
        this.dueDate = dueDate;
        this.interestCharges = createIfNull(interestCharges);
        this.loanFees = createIfNull(loanFees);
        this.principleMinimumPayments = createIfNull(principleMinimumPayments);

        this.totalInterestCharges = sumAmount(this.interestCharges);
        this.totalFees = sumAmount(this.loanFees);
        this.totalPrinciple = sumAmount(this.principleMinimumPayments);
        this.total = this.totalInterestCharges.add(this.totalFees).add(this.totalPrinciple);
    }

    @Override
    public void handle() {
        // nothing to do - informational event
    }

    private BigDecimal sumAmount(List<? extends HasAmount> list) {
        return list.stream().map(HasAmount::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
}
