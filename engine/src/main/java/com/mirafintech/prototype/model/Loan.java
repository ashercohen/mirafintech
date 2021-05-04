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
@Table(name = "LOANS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends EntityBase<Loan> {

    @Id
    private Long id;

    private Integer timestamp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Consumer consumer;

    private BigDecimal amount;

    private String type;

    private String status;

    private BigDecimal fraudScore; // TODO: rethink on this. fraud or risk? how do we maintain history of the loan risk (keep list and get latest)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Tranche tranche;

    @OneToMany(mappedBy = "loan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Charge> charges = new ArrayList<>();

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
