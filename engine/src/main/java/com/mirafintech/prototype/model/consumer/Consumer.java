package com.mirafintech.prototype.model.consumer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.OneToManyEntityAssociation;
import com.mirafintech.prototype.model.Payee;
import com.mirafintech.prototype.model.charge.ConsumerCharge;
import com.mirafintech.prototype.model.charge.LatePaymentFee;
import com.mirafintech.prototype.model.consumer.event.ConsumerEvent;
import com.mirafintech.prototype.model.credit.DatedCreditScore;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Table(name = "CONSUMER")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consumer implements OneToManyEntityAssociation, Payee {

    @Id
    private Long id;

    private Integer limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private Integer education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private Integer sex; // (1=male, 2=female)

    private Integer martialStatus; // (1=married, 2=single, 3=others)

    private Integer age; // int years

    private Integer billingCycleStartDay;

    private LocalDateTime addedAt;

    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "consumer_fk")
    private List<DatedCreditScore> datedCreditScores = new ArrayList<>();
    // TODO: addTimedCreditScore() + removeTimedCreditScore() - see test for example

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Payment> payments = new ArrayList<>();
    // TODO: addPayment() + removePayment()

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumerEvent> eventLog = new ArrayList<>();
    // TODO: add removeXXXEvent() ???

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ConsumerCharge> charges = new ArrayList<>();
    // TODO: addXXXt() + removeXXX()

    private Consumer(Long id,
                     Integer limitBalance,
                     Integer education,
                     Integer sex,
                     Integer martialStatus,
                     Integer age,
                     Integer billingCycleStartDay,
                     LocalDateTime addedAt,
                     BigDecimal balance,
                     List<DatedCreditScore> datedCreditScores,
                     List<Loan> loans,
                     List<Payment> payments,
                     List<ConsumerEvent> eventLog,
                     List<ConsumerCharge> charges) {
        this.id = id;
        this.limitBalance = limitBalance;
        this.education = education;
        this.sex = sex;
        this.martialStatus = martialStatus;
        this.age = age;
        this.billingCycleStartDay = billingCycleStartDay;
        this.addedAt = addedAt;
        this.balance = balance;
        this.datedCreditScores = createIfNull(datedCreditScores);
        this.loans = createIfNull(loans);
        this.payments = createIfNull(payments);
        this.eventLog = createIfNull(eventLog);
        this.charges = createIfNull(charges);
    }

    public Consumer(ConsumerDto dto, DatedCreditScore creditScore, Integer billingCycleStartDay, LocalDateTime timestamp) {
        this(dto.getId(),
             dto.getLimitBalance(),
             dto.getEducation(),
             dto.getSex(),
             dto.getMartialStatus(),
             dto.getAge(),
             billingCycleStartDay,
             timestamp,
             BigDecimal.ZERO,
             new ArrayList<>(List.of(creditScore)),
             null,
             null,
             null,
             null);
    }

    public DatedCreditScore currentCreditScore() {
        return this.datedCreditScores
                .stream()
                .max(Comparator.comparing(DatedCreditScore::getTimestamp))
                .orElseThrow(() -> new RuntimeException("consumer does not have credit score: id=" + this.id));
    }

    public Optional<DatedCreditScore> creditScoreAt(LocalDateTime localDateTime) {
        return this.datedCreditScores
                .stream()
                .filter(score -> score.getTimestamp().equals(localDateTime))
                .findAny(); // TODO: assuming user has max one score at specified time
    }

    public Optional<Payment> latestPayment() {
        // TODO: this should match also the date of the latest corresponding event
        return this.payments.stream().max(Comparator.comparing(Payment::getTimestamp).reversed());
    }

    @Override
    public void accept(PaymentAllocation paymentAllocation) {
        // TODO: implement
        return;
    }

    public boolean addConsumerEvent(ConsumerEvent event) {
        return addToCollection(this.eventLog, event, this, "event", event::setConsumer);
    }

    public boolean addLoan(Loan loan) {
        return addToCollection(this.loans, loan, this, "loan", loan::setConsumer);
    }

    public boolean hasLoan(Loan loan) {
        return this.loans.stream().anyMatch(l -> l.getId().longValue() == loan.getId().longValue());
    }

    public List<LatePaymentFee> unpaidFees() {
        return this.charges.stream()
                .filter(ConsumerCharge::isPending)
                .filter(c -> c instanceof LatePaymentFee)
                .map(c -> (LatePaymentFee)c)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consumer that = (Consumer) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
