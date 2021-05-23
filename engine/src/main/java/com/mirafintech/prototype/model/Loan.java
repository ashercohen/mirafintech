package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "LOAN")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends EntityBase<Loan> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

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
    private List<TimedRiskScore> timedRiskScores;

//    @OneToMany(mappedBy = "tranche", cascade = {CascadeType.ALL}, orphanRemoval = true)
//    private List<RiskLevel> riskLevels;

//    private String type;
//    private String status;
//    private BigDecimal fraudScore; // TODO: rethink on this. fraud or risk? how do we maintain history of the loan risk (keep list and get latest)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //TODO: make many2many as a loan might move between tranches
    private Tranche tranche; //TODO: add support for tranche history

    @OneToMany(mappedBy = "loan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Charge> charges = new ArrayList<>();

    private Loan(Long id,
                 LocalDateTime timestamp,
                 Consumer consumer,
                 BigDecimal amount,
                 List<TimedRiskScore> timedRiskScores,
                 Merchant merchant,
                 Tranche tranche,
                 List<PaymentAllocation> paymentAllocations,
                 List<Charge> charges) {
        this.id = id;
        this.timestamp = timestamp;
        this.consumer = consumer;
        this.amount = amount;
        this.timedRiskScores = timedRiskScores == null ? new ArrayList<>() : timedRiskScores;
        this.merchant = merchant;
        this.tranche = tranche; //TODO: add support for tranche history
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
