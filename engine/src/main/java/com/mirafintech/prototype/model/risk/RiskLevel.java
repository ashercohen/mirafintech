package com.mirafintech.prototype.model.risk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.OneToManyEntityAssociation;
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
public class RiskLevel implements OneToManyEntityAssociation {

    @Id
    private Long id;

    // uni-directional many-to-one:  RiskLevel n --> 1 RiskScore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "lowerBound_fk")
    private RiskScore lowerBound;

    // uni-directional many-to-one:  RiskLevel n --> 1 RiskScore
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
