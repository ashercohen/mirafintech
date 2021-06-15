package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "LOAN")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends EntityBase<Loan> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long externalId;

    private LocalDateTime timestamp;

    private BigDecimal amount;

    @JsonIgnore
    @Setter(value = AccessLevel.PROTECTED)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
    private Consumer consumer;

    @JsonIgnore
    @Setter(value = AccessLevel.PRIVATE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
    private Merchant merchant;

    /**
     * maintains the history of (timed) risk levels associated with this loan
     * to get current risk level use 'currentRiskLevel()'
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    @Getter(value = AccessLevel.PRIVATE)
    private List<DatedRiskScore> datedRiskScores;
    // TODO: addXXX() + removeXXX() ????

    /**
     * maintains the history of (dated) tranches associated with this loan
     * to get current tranche this loan belongs to use 'currentTranche()'
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "loan_fk")
    @Getter(value = AccessLevel.PRIVATE)
    private List<DatedTranche> trancheHistory = new ArrayList<>();
    // TODO: addXXX() + removeXXX() ????

    @JsonIgnore
    @OneToMany(mappedBy = "loan", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();
    // TODO: addPaymentAllocation() + removePaymentAllocation()

    @JsonIgnore
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Charge> charges = new ArrayList<>();
    // TODO: addCharge() + removeCharge()


    private Loan(Long id,
                 Long externalId,
                 LocalDateTime timestamp,
                 BigDecimal amount,
                 Consumer consumer,
                 Merchant merchant,
                 List<DatedRiskScore> datedRiskScores,
                 List<DatedTranche> trancheHistory,
                 List<PaymentAllocation> paymentAllocations,
                 List<Charge> charges) {
        this.id = id;
        this.externalId = externalId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.consumer = consumer;
        this.merchant = merchant;
        this.datedRiskScores = datedRiskScores == null ? new ArrayList<>() : datedRiskScores;
        this.trancheHistory = trancheHistory == null ? new ArrayList<>() : trancheHistory;
        this.paymentAllocations = paymentAllocations == null ? new ArrayList<>() : paymentAllocations;
        this.charges = charges == null ? new ArrayList<>() : charges;
    }

    public Loan(long externalId,
                LocalDateTime timestamp,
                Consumer consumer,
                BigDecimal amount,
                DatedRiskScore datedRiskScore,
                Merchant merchant) {
        this(null, externalId, timestamp, amount, consumer, merchant, new ArrayList<>(List.of(datedRiskScore)), null, null, null);
    }

    public DatedRiskScore currentRiskScore() {
        return this.datedRiskScores.stream()
                .max(Comparator.comparing(DatedRiskScore::getTimestamp).reversed())
                .orElseThrow(() -> new RuntimeException("could not find current risk score: loan id=" + this.id));
    }

    public List<DatedRiskScore> riskScoreHistory() {
        return this.datedRiskScores.stream().sorted(Comparator.comparing(DatedRiskScore::getTimestamp).reversed()).toList();
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
