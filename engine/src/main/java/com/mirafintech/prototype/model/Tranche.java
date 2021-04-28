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
public class Tranche implements Parent<Transaction> {

    @Id
    private Long id;

    private BigDecimal initialDebt;

    private BigDecimal currentDebt;

    private Integer rank;

    private String type; // Maintained / Diminished

    @OneToMany(mappedBy = "tranche", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Exchange exchange;

    public boolean addTransaction(Transaction transaction) {
        return addToCollection(this.transactions, transaction, this, "transaction", transaction::setTranche);
//        return Optional.ofNullable(transaction)
//                .map(t -> {
//                    t.setTranche(this);
//                    return transactions.add(t);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("transaction is null"));
    }

    public boolean removeTransaction(Transaction transaction) {
        return removeFromCollection(this.transactions, transaction, "transaction", transaction::setTranche);
//        return Optional.ofNullable(transaction)
//                .map(t -> {
//                    t.setTranche(null);
//                    return transactions.remove(transaction);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("transaction is null"));
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
