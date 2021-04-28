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
public class Merchant implements Parent<Transaction> {

    @Id
    private Long id;

    @OneToMany(mappedBy = "merchant", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public boolean addTransaction(Transaction transaction) {
        return addToCollection(this.transactions, transaction, this, "transaction", transaction::setMerchant);
    }

    public boolean removeTransaction(Transaction transaction) {
        return removeFromCollection(this.transactions, transaction, "transaction", transaction::setMerchant);
    }
}
