package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "RISK_LEVEL")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskLevel extends EntityBase<RiskLevel> {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "lowerBound_fk")
    private RiskScore lowerBound;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "upperBound_fk")
    private RiskScore upperBound;

    public RiskLevel(Long id, RiskScore lowerBound, RiskScore upperBound) {
        this.id = id;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public boolean contains(RiskScore riskScore) {
        return this.lowerBound.getValue() <= riskScore.getValue() && riskScore.getValue() < this.upperBound.getValue();
    }
}
