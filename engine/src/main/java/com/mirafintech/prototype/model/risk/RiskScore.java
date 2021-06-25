package com.mirafintech.prototype.model.risk;

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
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskScore {

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

    @Override
    public String toString() {
        return new StringBuffer("RiskScore{")
                .append("value=").append(value)
                .append('}')
                .toString();
    }
}
