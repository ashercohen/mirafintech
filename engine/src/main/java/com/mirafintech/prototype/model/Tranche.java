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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "TRANCHES")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tranche extends EntityBase<Tranche> {

    enum Status {
        ACTIVE, NOT_ACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal initialValue;

    private LocalDateTime creationDate; // virtual time TODO: maybe change to SystemTime

    private BigDecimal currentDebt;

    /**
     * maintains the history of the risk levels associated with this trnache
     * to get current risk level use 'currentRiskLevel()'
     */
    @OneToMany(mappedBy = "tranche", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<RiskLevel> riskLevels;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "tranche", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    // TODO:
    //  for each loan in the list, we need to mark it as "in" / "not in" the tranche
    //  because:
    //  1. loan might be removed from this tranche to another (change in risk is one use case)
    //  2. we agreed, in this prototype, that we don't change the state of the entities (= keep full history
    //     so we can analyze/learn/debug/...
    //  one possible solution would be to add another layer of indirection: instead of having a list of loans
    //  we can have a list of an entities each of them contains one loan and additional information like the
    //  flag "is in tranche" as well as "timestamp inserted/removed to/from tranche"

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Exchange exchange;

    private Tranche(Long id,
                    BigDecimal initialValue,
                    LocalDateTime creationDate,
                    BigDecimal currentDebt,
                    List<RiskLevel> riskLevels,
                    Status status,
                    List<Loan> loans,
                    Exchange exchange) {
        this.id = id;
        this.initialValue = initialValue;
        this.creationDate = creationDate;
        this.currentDebt = currentDebt;
        this.riskLevels = riskLevels;
        this.status = status;
        this.loans = loans;
        this.exchange = exchange;
    }

    public Tranche(BigDecimal initialValue, RiskLevel riskLevel, LocalDateTime creationDate) {
        this(null, initialValue, creationDate, BigDecimal.ZERO, new ArrayList<>(List.of(riskLevel)), Status.ACTIVE, new ArrayList<>(), null);
    }

    public RiskLevel currentRiskLevel() {
        return this.riskLevels.stream()
                .max(Comparator.comparing(RiskLevel::getStartDate))
                .orElseThrow(() -> new RuntimeException("current risk level not available"));
    }

    public List<RiskLevel> riskLevelHistory() {
        return this.riskLevels.stream().sorted(Comparator.comparing(RiskLevel::getStartDate).reversed()).toList();
    }

    public boolean addLoan(Loan loan) {
        return addToCollection(this.loans, loan, this, "loan", loan::setTranche);
    }

    public boolean removeLoan(Loan loan) {
        return removeFromCollection(this.loans, loan, "loan", loan::setTranche);
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
