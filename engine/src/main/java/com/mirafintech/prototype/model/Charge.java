package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CHARGE")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Charge extends EntityBase<Charge> {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Loan loan;

    private Integer timestamp;

    private BigDecimal interest;

    private BigDecimal fee;

    private Charge(Long id, Loan loan, Integer timestamp, BigDecimal interest, BigDecimal fee) {
        this.id = id;
        this.loan = loan;
        this.timestamp = timestamp;
        this.interest = interest;
        this.fee = fee;
    }
}
