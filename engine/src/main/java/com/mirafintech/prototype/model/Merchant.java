package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.loan.Loan;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Entity
@Table(name = "MERCHANT")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Merchant {

    @Id
    private Long id;

    private String name;

    private LocalDateTime since;

    @Setter(value = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
    // TODO: addLoan() + removeLoan()

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
        // not calling addToCollection() since this merchant is already set at loan
        return Optional.ofNullable(loan)
                .map(this.loans::add)
                .orElseThrow(() -> new IllegalArgumentException("loan is null"));
    }

    // TODO: probably not required as this operation not supported
    public boolean removeLoan(Loan loan) {
        throw new RuntimeException("Merchant::removeLoan not implemented yet");
    }

    public List<Long> getLoanIds() {
        return this.loans.stream().map(Loan::getId).toList();
    }
}
