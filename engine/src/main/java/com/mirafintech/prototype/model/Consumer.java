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
public class Consumer implements Parent<Transaction>{

    @Id
    private Long id;

    private int limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private int education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private int sex; // (1=male, 2=female)

    private int martialStatus; // (1=married, 2=single, 3=others)

    private int age; // int years

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public boolean addTransaction(Transaction transaction) {
        return addToCollection(this.transactions, transaction, this, "transaction", transaction::setConsumer);
    }

    public boolean removeTransaction(Transaction transaction) {
        return removeFromCollection(this.transactions, transaction, "transaction", transaction::setConsumer);
    }
}
