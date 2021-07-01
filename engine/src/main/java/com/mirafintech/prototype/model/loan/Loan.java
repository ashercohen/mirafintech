package com.mirafintech.prototype.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Dated;
import com.mirafintech.prototype.model.DatedBalance;
import com.mirafintech.prototype.model.Merchant;
import com.mirafintech.prototype.model.Payee;
import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.charge.LoanCharge;
import com.mirafintech.prototype.model.charge.LoanFee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.interest.*;
import com.mirafintech.prototype.model.loan.event.InterestChargeAddedLoanEvent;
import com.mirafintech.prototype.model.loan.event.LoanEvent;
import com.mirafintech.prototype.model.loan.event.PaymentAllocationAddedLoanEvent;
import com.mirafintech.prototype.model.payment.allocation.LoanPaymentAllocation;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import com.mirafintech.prototype.model.risk.DatedRiskScore;
import com.mirafintech.prototype.model.tranche.Tranche;
import com.mirafintech.prototype.model.tranche.event.DatedTranche;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.mirafintech.prototype.model.AssociationHelper.addToCollection;
import static com.mirafintech.prototype.model.AssociationHelper.createIfNull;


@Entity
@Table(name = "LOAN")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan implements Payee {

    enum Status {
        ACTIVE, NOT_ACTIVE, PAID_IN_FULL // TODO: maybe add more states: DEFAULTED, ...
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long externalId;

    private LocalDateTime creationDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
    private Consumer consumer;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    @Setter(value = AccessLevel.PRIVATE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
    private Merchant merchant;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    private List<DatedBalance> balanceHistory = new ArrayList<>();

    /**
     * maintains the history of (timed) risk levels associated with this loan
     * to get current risk level use 'currentRiskLevel()'
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    private List<DatedRiskScore> riskScoreHistory;

    /**
     * maintains the history of tranches (actual type:  DatedTranche) associated with this loan
     * to get current tranche this loan belongs to use 'currentTranche()'
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    @Getter(value = AccessLevel.PRIVATE)
    private List<DatedTranche> trancheHistory = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<LoanPaymentAllocation> loanPaymentAllocations = new ArrayList<>();

    // NOTE: eagerly fetch to support simple serialization
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    private List<LoanCharge<? extends LoanPaymentAllocation>> loanCharges = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<LoanEvent> eventLog = new ArrayList<>();

    protected Loan() {
    }

    private Loan(Long id,
                 Long externalId,
                 LocalDateTime creationDate,
                 Consumer consumer,
                 BigDecimal amount,
                 Status status,
                 Merchant merchant,
                 List<DatedBalance> balanceHistory,
                 List<DatedRiskScore> riskScoreHistory,
                 List<DatedTranche> trancheHistory,
                 List<LoanPaymentAllocation> loanPaymentAllocations,
                 List<LoanCharge<? extends LoanPaymentAllocation>> loanCharges,
                 List<LoanEvent> eventLog) {
        this.id = id;
        this.externalId = externalId;
        this.creationDate = creationDate;
        this.consumer = consumer;
        this.amount = amount;
        this.status = status;
        this.merchant = merchant;
        this.balanceHistory = createIfNull(balanceHistory);
        this.riskScoreHistory = createIfNull(riskScoreHistory);
        this.trancheHistory = createIfNull(trancheHistory);
        this.loanPaymentAllocations = createIfNull(loanPaymentAllocations);
        this.loanCharges = createIfNull(loanCharges);
        this.eventLog = createIfNull(eventLog);
    }

    public Loan(long externalId,
                LocalDateTime creationDate,
                Consumer consumer,
                BigDecimal amount,
                DatedRiskScore datedRiskScore,
                Merchant merchant) {
        this(null, externalId,creationDate, consumer, amount, Status.ACTIVE, merchant,
                new ArrayList<>(List.of(new DatedBalance(creationDate, amount))),
                new ArrayList<>(List.of(datedRiskScore)), null, null, null, null);
    }

    /**
     * deposit positive amount at given date
     * return the updated loan balance if successful or previous balance if not
     */
    //TODO: maybe we can make this package protected
    public BigDecimal deposit(BigDecimal amount, LocalDateTime timestamp) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("illegal deposit amount: " + amount);
        }

        return addDatedBalance(new DatedBalance(timestamp, currentBalance().subtract(amount))).getBalance();
    }

    private DatedBalance addDatedBalance(DatedBalance datedBalance) {
        return this.balanceHistory.add(datedBalance)
                ? datedBalance
                : currentDatedBalance();
    }

    @Override
    public void accept(PaymentAllocation paymentAllocation) {

        if (!(paymentAllocation instanceof LoanPaymentAllocation loanPaymentAllocation)) {
            throw new IllegalArgumentException("paymentAllocation wrong sub-type. expected: LoanPaymentAllocation actual: " + paymentAllocation.getClass().getSimpleName());
        }

        addPaymentAllocation(loanPaymentAllocation);
    }

    public DatedRiskScore currentRiskScore() {
        return this.riskScoreHistory.stream()
                .max(Comparator.comparing(DatedRiskScore::getTimestamp).reversed())
                .orElseThrow(() -> new RuntimeException("could not find current risk score: loan id=" + this.id));
    }

    public List<DatedRiskScore> riskScoreHistory() {
        return this.riskScoreHistory.stream().sorted(Comparator.comparing(DatedRiskScore::getTimestamp).reversed()).toList();
    }

    public boolean setCurrentTranche(Tranche tranche, LocalDateTime timestamp) { //TODO: check if opposite direction assignment is needed
        return this.trancheHistory.add(new DatedTranche(timestamp, tranche));
    }

    public AnnualInterestIntervalList<?> interestIntervalList(RawInterval interval) {

        record DatedInterest(LocalDateTime timestamp, APR apr) implements Dated<APR, APRInterestIntervalList.Interval, APRInterestIntervalList> {

            // instance to call default methods from static context
            private static final DatedInterest DATED_INTEREST = new DatedInterest(LocalDateTime.now(), APR.ZERO);

            public static APRInterestIntervalList getInterestHistory(List<DatedInterest> list, LocalDate from, LocalDate to) {
                return DATED_INTEREST.getHistory(list, from, to);
            }

            public static Stream<APRInterestIntervalList.Interval> toIntervalStream(List<DatedInterest> sortedHistory, LocalDate to) {
                return DATED_INTEREST.toIntervals(sortedHistory, to);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return timestamp();
            }

            @Override
            public APR getV() {
                return apr();
            }

            @Override
            public APRInterestIntervalList.Interval newInterval(LocalDate from, LocalDate to, APR value) {
                return new APRInterestIntervalList.Interval(from, to, value);
            }

            @Override
            public DatedInterest newDummyInstance(LocalDateTime timestamp) {
                return new DatedInterest(timestamp, APR.ZERO);
            }

            @Override
            public APRInterestIntervalList newIntervalList(List<APRInterestIntervalList.Interval> intervals) {
                return new APRInterestIntervalList(intervals);
            }
        }

        List<DatedInterest> datedInterests = this.trancheHistory.stream()
                .map(datedTranche ->
                        new DatedInterest(
                                datedTranche.getTimestamp(),
                                new APR(datedTranche.getTranche().getInterest(), BigDecimal.ZERO)
                        )
                ).toList();

        return DatedInterest.getInterestHistory(datedInterests, interval.from(), interval.to());
    }

    public BigDecimal currentBalance() {
        DatedBalance currentDatedBalance = currentDatedBalance();
        return currentDatedBalance.getBalance();
    }

    public BalanceIntervalList balanceIntervalList(RawInterval interval) {
        return DatedBalance.getBalanceHistory(this.balanceHistory, interval.from(), interval.to());
    }

    public List<DatedBalance> getBalanceHistory(LocalDateTime dateTime) {
        return this.balanceHistory.stream()
                .filter(datedBalance -> datedBalance.getTimestamp().isAfter(dateTime))
                .sorted(Comparator.comparing(DatedBalance::getTimestamp))
                .toList();
    }

    public List<LoanFee> getUnpaidLoanFees() {
        return this.loanCharges.stream()
                .filter(LoanCharge::isPending)
                .filter(c -> c instanceof LoanFee)
                .map(c -> (LoanFee) c)
                .toList();
    }

    // TODO: divide into tranche interest and mira interest
    @JsonIgnore
    public List<InterestCharge> getUnpaidInterestCharges() {
        return this.loanCharges.stream()
                .filter(LoanCharge::isPending)
                .filter(c -> c instanceof InterestCharge)
                .map(c -> (InterestCharge) c)
                .toList();
    }

    public Tranche currentTranche() {
        return this.trancheHistory.stream()
                .max(Comparator.comparing(DatedTranche::getTimestamp))
                .map(DatedTranche::getTranche)
                .orElseThrow(() -> new RuntimeException("could not find current tranche: loan id=" + this.id));
    }

    public List<DatedTranche> trancheHistory() {
        return this.trancheHistory.stream().sorted(Comparator.comparing(DatedTranche::getTimestamp).reversed()).toList();
    }

    public List<LoanCharge> unpaidFees() {
        // TODO: implement
        // TODO: need to maintain whether a fee has been paid or not
        throw new RuntimeException("not implemented yet");
    }

//    events should not be added externally: when we add PA, Charge, ... event should be generated internally and added to the event log.
    private boolean addToEventLog(LoanEvent event) {
        return addToCollection(this.eventLog, event, this, "event", event::setLoan);
    }

    private boolean addPaymentAllocation(LoanPaymentAllocation paymentAllocation) {

        PaymentAllocationAddedLoanEvent paymentAllocationAddedLoanEvent = new PaymentAllocationAddedLoanEvent(paymentAllocation, this, paymentAllocation.getTimestamp(), "payment_received");
        addToEventLog(paymentAllocationAddedLoanEvent);
        paymentAllocationAddedLoanEvent.handle();

        return this.loanPaymentAllocations.add(paymentAllocation);
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public boolean addLoanCharge(LoanCharge<?> loanCharge) {

        // sanity check
        if (loanCharge.getLoan() == null) {
            throw new RuntimeException("LoanCharge.loan is null");
        }
        // another sanity check
        if (loanCharge.getLoan() != this) {
            throw new RuntimeException(String.format("unexpected loan: expected=%d actual=%d", this.id, loanCharge.getLoan().id));
        }

        if (!(loanCharge instanceof InterestCharge interestCharge)) {
            throw new RuntimeException("charge not supported: " + loanCharge.getClass().getSimpleName());
        }

        InterestChargeAddedLoanEvent interestChargeEvent = new InterestChargeAddedLoanEvent(interestCharge.getTimestamp(), this, "interest_calculation", interestCharge);
        addToEventLog(interestChargeEvent);
        interestChargeEvent.handle();

        return this.loanCharges.add(loanCharge);
    }

    public Long getConsumerId() {
        return this.consumer.getId();
    }

    public Long getMerchantId() {
        return this.merchant.getId();
    }

    public List<String> getRiskScoreSummary() {
        return this.riskScoreHistory.stream().map(DatedRiskScore::toString).toList();
    }

    private DatedBalance currentDatedBalance() {
        return this.balanceHistory.stream()
                .max(Comparator.comparing(DatedBalance::getTimestamp))
                .orElseThrow(() -> new RuntimeException("empty balance history for loan id=" + this.id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan that = (Loan) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
