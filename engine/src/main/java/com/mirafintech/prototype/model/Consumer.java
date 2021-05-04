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
@Table(name = "CONSUMERS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consumer implements Parent<Loan>{

    @Id
    private Long id;

    private int limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private int education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private int sex; // (1=male, 2=female)

    private int martialStatus; // (1=married, 2=single, 3=others)

    private int age; // int years

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public boolean addTransaction(Loan loan) {
        return addToCollection(this.loans, loan, this, "transaction", loan::setConsumer);
    }

    public boolean removeTransaction(Loan loan) {
        return removeFromCollection(this.loans, loan, "transaction", loan::setConsumer);
    }
}
