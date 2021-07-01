package com.mirafintech.prototype.model.consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.DatedBalance;
import com.mirafintech.prototype.model.Payee;
import com.mirafintech.prototype.model.charge.Charge;
import com.mirafintech.prototype.model.charge.ConsumerCharge;
import com.mirafintech.prototype.model.charge.LatePaymentFee;
import com.mirafintech.prototype.model.consumer.event.ConsumerEvent;
import com.mirafintech.prototype.model.consumer.event.LoanAddedConsumerEvent;
import com.mirafintech.prototype.model.consumer.event.MinimumPaymentConsumerEvent;
import com.mirafintech.prototype.model.consumer.event.PaymentAllocationAddedConsumerEvent;
import com.mirafintech.prototype.model.credit.DatedCreditScore;
import com.mirafintech.prototype.model.interest.BalanceIntervalList;
import com.mirafintech.prototype.model.interest.BalanceIntervalListImpl;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.model.payment.PaymentDetails;
import com.mirafintech.prototype.model.payment.allocation.ConsumerPaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static com.mirafintech.prototype.model.AssociationHelper.addToCollection;
import static com.mirafintech.prototype.model.AssociationHelper.createIfNull;


@Entity
@Table(name = "CONSUMER")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consumer implements Payee {

    @Id
    private Long id;

    private Integer limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private Integer education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private Integer sex; // (1=male, 2=female)

    private Integer martialStatus; // (1=married, 2=single, 3=others)

    private Integer age; // int years

    private Integer billingCycleStartDay;

    private LocalDateTime addedAt;

    /**
     * consumer balance is not maintained neither as a single value nor as a 'balanceHistory' (like with Loan)
     * instead, it is calculated on demand - see getBalance() below
     */

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "consumer_fk")
    private List<DatedCreditScore> datedCreditScores = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    private List<Loan> loans = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    private List<ConsumerCharge<? extends ConsumerPaymentAllocation>> charges = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<ConsumerPaymentAllocation> consumerPaymentAllocations = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "consumer_fk")
    private List<DatedBalance> balanceHistory = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumerEvent> eventLog = new ArrayList<>();

    private Consumer(Long id,
                     Integer limitBalance,
                     Integer education,
                     Integer sex,
                     Integer martialStatus,
                     Integer age,
                     Integer billingCycleStartDay,
                     LocalDateTime addedAt,
                     List<DatedCreditScore> datedCreditScores,
                     List<Loan> loans,
                     List<Payment> payments,
                     List<ConsumerCharge<? extends ConsumerPaymentAllocation>> charges,
                     List<DatedBalance> balanceHistory,
                     List<ConsumerEvent> eventLog) {
        this.id = id;
        this.limitBalance = limitBalance;
        this.education = education;
        this.sex = sex;
        this.martialStatus = martialStatus;
        this.age = age;
        this.billingCycleStartDay = billingCycleStartDay;
        this.addedAt = addedAt;
        this.datedCreditScores = createIfNull(datedCreditScores);
        this.loans = createIfNull(loans);
        this.payments = createIfNull(payments);
        this.charges = createIfNull(charges);
        this.balanceHistory = createIfNull(balanceHistory);
        this.eventLog = createIfNull(eventLog);
    }

    public Consumer(ConsumerDto dto, DatedCreditScore creditScore, Integer billingCycleStartDay, LocalDateTime timestamp) {
        this(dto.id(),
             dto.limitBalance(),
             dto.education(),
             dto.sex(),
             dto.martialStatus(),
             dto.age(),
             billingCycleStartDay,
             timestamp,
             new ArrayList<>(List.of(creditScore)),
             null,
             null,
             null,
             null,
             null);
    }

    public BigDecimal getBalance() {

        // sum all loans balances
        BigDecimal loansBalance = this.loans.stream()
                .map(Loan::currentBalance)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // sum all pending (not paid) charges balances
        BigDecimal chargesBalance = this.charges.stream()
                .filter(ConsumerCharge::isPending)
                .map(ConsumerCharge::balance)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        return loansBalance.add(chargesBalance).negate();
    }

    public BalanceIntervalList getBalanceIntervalList(LocalDate from, LocalDate to) {

        return null;//DatedBalance.getBalanceHistory(this.balanceHistory, from, to);
    }

    private BigDecimal currentBalance() {
        return this.balanceHistory.stream().max(Comparator.comparing(DatedBalance::getTimestamp)).map(DatedBalance::getBalance).orElse(BigDecimal.ZERO);
    }

    private void updateBalance(Loan newLoan, LocalDateTime timestamp) {
        doUpdateBalance(newLoan.getAmount(), timestamp);
    }

    private void updateBalance(Charge newCharge, LocalDateTime timestamp) {
        doUpdateBalance(newCharge.getAmount(), timestamp);
    }

    private void updateBalance(Payment payment, LocalDateTime timestamp) {

        BigDecimal paymentAllocationsSum = payment.getPaymentAllocations().stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("could not sum payment allocations"));

        // sanity
        if (payment.getAmount().compareTo(paymentAllocationsSum) != 0) {
            throw new RuntimeException("sum of payment allocations does not match payment amount");
        }

        doUpdateBalance(paymentAllocationsSum, timestamp);
    }

    private void doUpdateBalance(BigDecimal amountToAdd, LocalDateTime timestamp) {
        this.balanceHistory.add(new DatedBalance(timestamp, currentBalance().add(amountToAdd)));
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

    public boolean addLoan(Loan loan, LocalDateTime timestamp) {
        // create event
        LoanAddedConsumerEvent event = new LoanAddedConsumerEvent(loan, this, timestamp, "loan_added");
        event.handle();
        writeToEventLog(event);

        return addToCollection(this.loans, loan, this, "loan", loan::setConsumer);
    }

    public boolean addMinimumPaymentEvent(MinimumPaymentConsumerEvent event) {
        return writeToEventLog(event);
    }

    public Optional<Payment> latestPayment() {
        // TODO: this should match also the date of the latest corresponding event
        return this.payments.stream().max(Comparator.comparing(Payment::getTimestamp).reversed());
    }

    @Override
    public void accept(PaymentAllocation paymentAllocation) {

        if (!(paymentAllocation instanceof ConsumerPaymentAllocation consumerPaymentAllocation)) {
            throw new IllegalArgumentException("paymentAllocation wrong sub-type. expected: LoanPaymentAllocation actual: " + paymentAllocation.getClass().getSimpleName());
        }

        addPaymentAllocation(consumerPaymentAllocation);
    }

    private boolean addPaymentAllocation(ConsumerPaymentAllocation paymentAllocation) {

        PaymentAllocationAddedConsumerEvent consumerEvent =
                new PaymentAllocationAddedConsumerEvent(paymentAllocation.getTimestamp(), this, "payment_received", paymentAllocation);
        consumerEvent.handle();
        writeToEventLog(consumerEvent);

        return this.consumerPaymentAllocations.add(paymentAllocation);
    }

    private boolean writeToEventLog(ConsumerEvent event) {
        return addToCollection(this.eventLog, event, this, "event", event::setConsumer);
    }

    public boolean isLoanAlreadyExists(Loan loan) {
        return this.loans.stream()
                .anyMatch(l -> l.getExternalId().longValue() == loan.getExternalId().longValue());
    }

    // TODO: remove if unused
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

    public List<Long> getLoanIds() {
        return this.loans.stream().map(Loan::getId).toList();
    }

    public List<PaymentDetails> getPaymentDetails() {
        return this.payments.stream().map(Payment::details).toList();
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
