package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Table(name = "LOAN")
@Getter
@Setter
//@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends EntityBase<Loan> /*implements Comparable<Loan>, Comparator<Loan>*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    @JsonIgnore // TODO: revisit this
    //TODO: verify bi-di association
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Consumer consumer;

    private BigDecimal amount;

    /**
     * maintains the history of (timed) risk levels associated with this loan
     * to get current risk level use 'currentRiskLevel()'
     */
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    @Getter(value = AccessLevel.PRIVATE)
    private List<TimedRiskScore> timedRiskScores;

//    @OneToMany(mappedBy = "tranche", cascade = {CascadeType.ALL}, orphanRemoval = true)
//    private List<RiskLevel> riskLevels;

//    private String type;
//    private String status;
//    private BigDecimal fraudScore; // TODO: rethink on this. fraud or risk? how do we maintain history of the loan risk (keep list and get latest)

    @JsonIgnore
    @Setter(value = AccessLevel.PRIVATE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //TODO: cascade???
    private Merchant merchant;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY, optional = true) //TODO: make many2many as a loan might move between tranches
//    private Tranche tranche; //TODO: add support for tranche history

//    @JsonIgnore
//    @Setter(value = AccessLevel.PRIVATE)
//    @ManyToMany(mappedBy = "loans")
//    @
//    private Set<Tranche> tranches = new TreeSet<>()

    /**
     * maintains the history of (dated) tranches associated with this loan
     * to get current tranche this loan belongs to use 'currentTranche()'
     */
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    @Getter(value = AccessLevel.PRIVATE)
    private List<DatedTranche> trancheHistory = new ArrayList<>();

    @JsonIgnore // TODO: revisit this
    @OneToMany(mappedBy = "loan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();


    // TODO: this association probably need to be bi-di
    @JsonIgnore // TODO: revisit this
    @OneToMany(mappedBy = "loan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Charge> charges = new ArrayList<>();

    private Loan(Long id,
                 LocalDateTime timestamp,
                 Consumer consumer,
                 BigDecimal amount,
                 List<TimedRiskScore> timedRiskScores,
                 Merchant merchant,
                 List<DatedTranche> trancheHistory,
                 List<PaymentAllocation> paymentAllocations,
                 List<Charge> charges) {
        this.id = id;
        this.timestamp = timestamp;
        this.consumer = consumer;
        this.amount = amount;
        this.timedRiskScores = timedRiskScores == null ? new ArrayList<>() : timedRiskScores;
        this.merchant = merchant;
        this.trancheHistory = trancheHistory == null ? new ArrayList<>() : trancheHistory;
        this.paymentAllocations = paymentAllocations;
        this.charges = charges;
    }

    public Loan(LocalDateTime timestamp,
                Consumer consumer,
                BigDecimal amount,
                TimedRiskScore timedRiskScore,
                Merchant merchant) {
        this(null, timestamp, consumer, amount, new ArrayList<>(List.of(timedRiskScore)), merchant, null, null, null);
    }

    public TimedRiskScore currentRiskScore() {
        return this.timedRiskScores.stream()
                .max(Comparator.comparing(TimedRiskScore::getTimestamp).reversed())
                .orElseThrow(() -> new RuntimeException("could not find current risk score: loan id=" + this.id));
    }

    public List<TimedRiskScore> riskScoreHistory() {
        return this.timedRiskScores.stream().sorted(Comparator.comparing(TimedRiskScore::getTimestamp).reversed()).toList();
    }

    public boolean setCurrentTranche(Tranche tranche, LocalDateTime timestamp) { //TODO: check if opposite direction assignment is needed
        return this.trancheHistory.add(new DatedTranche(timestamp, tranche));
    }

    public DatedTranche currentTranche() {
        return this.trancheHistory.stream()
                .max(Comparator.comparing(DatedTranche::getTimestamp))
                .orElseThrow(() -> new RuntimeException("could not find current tranche: loan id=" + this.id));
    }

    public List<DatedTranche> trancheHistory() {
        return this.trancheHistory.stream().sorted(Comparator.comparing(DatedTranche::getTimestamp).reversed()).toList();
    }

//    @Override
//    public int compareTo(Loan other) {
//        return this.timestamp.compareTo(other.timestamp);
//    }
//
//    @Override
//    public int compare(Loan o1, Loan o2) {
//        return 0;
//    }

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
