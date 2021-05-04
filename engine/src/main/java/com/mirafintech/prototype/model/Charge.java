package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CHARGES")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Charge extends EntityBase<Charge> {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Loan loan;

    private Integer timestamp;

    private BigDecimal interest;

    private BigDecimal fee;
}
