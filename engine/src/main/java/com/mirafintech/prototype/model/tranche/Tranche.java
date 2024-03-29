package com.mirafintech.prototype.model.tranche;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Exchange;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.risk.RiskLevel;
import com.mirafintech.prototype.model.risk.RiskScore;
import com.mirafintech.prototype.model.tranche.event.TrancheEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mirafintech.prototype.model.AssociationHelper.addToCollection;


@Entity
@Table(name = "TRANCHE")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tranche {

    public enum Status {
        ACTIVE, FULLY_ALLOCATED, NOT_ACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime creationDate;

    @Column(precision = 16, scale = 5)
    private BigDecimal value;

    @Column(precision = 16, scale = 5)
    private BigDecimal maxToleratedValue;

    @Column(precision = 16, scale = 5)
    private BigDecimal interest;

    // TODO: add history of currentDebt (rename for a better name - balance?)
    //  in addition, we should record any operation that changed the balance (type: withdrawal, deposit; which loan, timestamp, etc)
    @Column(precision = 16, scale = 5)
    private BigDecimal currentBalance;

    // uni-directional many-to-one:  Tranche n --> 1 RiskLevel
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "risklevel_fk")
    @JsonIgnore
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private Status status;

    // TODO:
    //  for each loan in the list, we need to mark it as "in" / "not in" the tranche
    //  because:
    //  1. loan might be removed from this tranche to another (change in risk is one use case)
    //  2. we agreed, in this prototype, that we don't change the state of the entities (= keep full history
    //     so we can analyze/learn/debug/...
    //  one possible solution would be to add another layer of indirection: instead of having a list of loans
    //  we can have a list of an entities each of them contains one loan and additional information like the
    //  flag "is in tranche" as well as "timestamp inserted/removed to/from tranche"

    // TODO: add support for history ("DatedLoan")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "tranche_fk")
    private List<Loan> loans = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private Exchange exchange;

    @OneToMany(mappedBy = "tranche", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrancheEvent> eventLog = new ArrayList<>();
    // TODO: addTrancheEvent() + removeTrancheEvent()

//    @JsonIgnore
//    @OneToMany(mappedBy = "tranche", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();
//    // TODO: addPaymentAllocation() + removePaymentAllocation()


    private Tranche(Long id,
                    LocalDateTime creationDate,
                    BigDecimal value,
                    BigDecimal maxToleratedValue,
                    BigDecimal interest,
                    BigDecimal initialBalance,
                    RiskLevel riskLevel,
                    Status status,
                    List<Loan> loans,
                    Exchange exchange,
                    List<TrancheEvent> eventLog) {
        this.id = id;
        this.creationDate = creationDate;
        this.value = value;
        this.maxToleratedValue = maxToleratedValue;
        this.interest = interest;
        this.currentBalance = initialBalance;
        this.riskLevel = riskLevel;
        this.status = status;
        this.loans = loans == null ? new ArrayList<>() : loans;
        this.exchange = exchange;
        this.eventLog = eventLog == null ? new ArrayList<>() : eventLog;
    }

    public Tranche(LocalDateTime timestamp,
                   BigDecimal value,
                   BigDecimal trancheBalanceTolerance,
                   BigDecimal interest,
                   long riskLevelId,
                   RiskScore lowerBound,
                   RiskScore upperBound) {

        this(null,
             timestamp,
             value,
             value.multiply(trancheBalanceTolerance.add(BigDecimal.ONE)),
             interest,
             BigDecimal.ZERO,
             new RiskLevel(riskLevelId, lowerBound, upperBound),
             Status.ACTIVE,
             null,
             null,
             null
        );
    }

    /**
     * "copy ctor"
     */
    public Tranche(LocalDateTime timestamp, Tranche other) {

        this(null,
             timestamp,
             other.value,
             other.maxToleratedValue,
             other.interest, // should we call resolveTrancheInterest() ?
             BigDecimal.ZERO,
             other.riskLevel,
             Status.ACTIVE,
             null,
             null,
             null
        );
    }

    public BigDecimal currentBalance() {
        // TODO: once currentBalance supports history update impl
        return this.currentBalance;
    }

    public boolean addLoan(Loan loan, LocalDateTime timestamp) {
        boolean loanAdded = this.loans.add(loan);
        boolean trancheSetAtLoan = loan.setCurrentTranche(this, timestamp);

        return loanAdded && trancheSetAtLoan;
    }

    public boolean removeLoan(Loan loan) {

//        return removeFromCollection(this.loans, loan, "loan", loan::setTranche);
        /**
         * TODO:
         *  - make sure Loan's identity is well defined (remove() relies on it)
         *  - do we need to update the association from the other side?
         */
        return this.loans.remove(loan);
    }

    public boolean addEventEvent(TrancheEvent event) {
        return addToCollection(this.eventLog, event, this, "event", event::setTranche);
    }

    public boolean removeTrancheEvent(TrancheEvent event) {
        throw new RuntimeException("Tranche::removeTrancheEvent operation not supported");
    }

    public boolean isFullyAllocated() {
        return this.currentBalance.compareTo(this.value) >= 0;
    }

    public void setStatus(Status status) {

        if (status.ordinal() - this.status.ordinal() != 1) {
            throw new RuntimeException(String.format("illegal status transition: current=%s, new=%s", this.status, status));
        }

        this.status = status;
    }

    /**
     * formatted view of the riskLevel
     * used for json serialization only
     * name format is also a hack (used by jackson)
     */
    public String get_riskLevel() {
        return this.riskLevel.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tranche tranche = (Tranche) o;
        return id.equals(tranche.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
