package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "MERCHANT")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Merchant extends EntityBase<Merchant> {

    @Id
    private Long id;

    private String name;

    private LocalDateTime since;

    @OneToMany(mappedBy = "merchant", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    private Merchant(Long id, String name, LocalDateTime since, List<Loan> loans) {
        this.id = id;
        this.name = name;
        this.since = since;
        this.loans = loans == null ? new ArrayList<>() : loans;
    }

    public Merchant(Long id, String name, LocalDateTime since) {
        this(id, name, since, null);
    }

    public boolean addLoan(Loan loan) {
        return addToCollection(this.loans, loan, this, "loans", loan::setMerchant);
    }

    public boolean removeLoan(Loan loan) {
        return removeFromCollection(this.loans, loan, "loans", loan::setMerchant);
    }
}
