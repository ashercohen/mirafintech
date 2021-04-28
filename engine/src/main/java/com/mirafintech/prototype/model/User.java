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
@Table(name = "USERS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Parent<Transaction> {

    @Id
    private Long id;

    private int limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private int education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private int sex; // (1=male, 2=female)

    private int martialStatus; // (1=married, 2=single, 3=others)

    private int age; // int years

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public boolean addTransaction(Transaction transaction) {

        return addToCollection(this.transactions, transaction, this, "transaction", transaction::setUser);

//        return Optional.ofNullable(transaction)
//                .map(t -> {
//                    t.setUser(this);
//                    return transactions.add(t);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("transaction is null"));
    }

    public boolean removeTransaction(Transaction transaction) {

        return removeFromCollection(this.transactions, transaction, "transaction", transaction::setUser);

//        return Optional.ofNullable(transaction)
//                .map(t -> {
//                    t.setUser(null);
//                    return transactions.remove(transaction);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("transaction is null"));
    }
}
