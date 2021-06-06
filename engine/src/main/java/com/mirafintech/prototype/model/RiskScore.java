package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Table(name = "RISK_SCORE")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskScore extends EntityBase<RiskScore> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Exclude
    private Long id;

    private int value; // [0..100]

    private RiskScore(Long id, int value) {
        this.id = id;
        this.value = value;
    }

    public RiskScore(int value) {
        this(null, value);
    }
}
