package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CHARGE")
@Getter
@Setter
//@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Charge extends EntityBase<Charge> {

    @Id
    private Long id;

    // TODO: this association should be bi-directional - fix!!!
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "loan_fk")
    private Loan loan;

    private Integer timestamp;

    private BigDecimal interest;

    private BigDecimal fee;
}
