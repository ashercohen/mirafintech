package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal initialValue;

    private BigDecimal currentDebt;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

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

    private Tranche(Long id, BigDecimal initialValue, BigDecimal currentDebt, RiskLevel riskLevel, Status status, List<Loan> loans, Exchange exchange) {
        this.id = id;
        this.initialValue = initialValue;
        this.currentDebt = currentDebt;
        this.riskLevel = riskLevel;
        this.status = status;
        this.loans = loans;
        this.exchange = exchange;
    }

    public Tranche(BigDecimal initialValue, RiskLevel riskLevel) {
        this(null, initialValue, BigDecimal.ZERO, riskLevel, Status.ACTIVE, new ArrayList<>(), null);
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
