package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;


@Entity
@Table(name = "TRANSACTIONS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    private BigDecimal amount;

    private String type;

    private String status;

    private BigDecimal fraudScore;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Tranche tranche;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
    private TransactionMeta meta;

    public void setMeta(TransactionMeta meta) {

        if (meta == null) {
            if (this.meta != null) {
                this.meta.setTransaction(null);
            }
        }
        else {
            meta.setTransaction(this);
        }
        this.meta = meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
