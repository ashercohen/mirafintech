package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "MERCHANTS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Merchant extends EntityBase<Merchant> {

    @Id
    private Long id;

    @OneToMany(mappedBy = "merchant", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    public boolean addLoan(Loan loan) {
        return addToCollection(this.loans, loan, this, "loans", loan::setMerchant);
    }

    public boolean removeLoan(Loan loan) {
        return removeFromCollection(this.loans, loan, "loans", loan::setMerchant);
    }
}
