package com.mirafintech.prototype.model.risk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.OneToManyEntityAssociation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "DATED_RISK_SCORE")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatedRiskScore implements OneToManyEntityAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    // uni-directional many-to-one:  DatedRiskScore n --> 1 RiskScore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "riskscore_fk")
    private RiskScore riskScore;

    private DatedRiskScore(Long id, LocalDateTime timestamp, RiskScore riskScore) {
        this.id = id;
        this.timestamp = timestamp;
        this.riskScore = riskScore;
    }

    public DatedRiskScore(LocalDateTime timestamp, RiskScore riskScore) {
        this(null, timestamp, riskScore);
    }

    @Override
    public String toString() {
        return new StringBuffer("DatedRiskScore{")
                .append("id=").append(id)
                .append(", timestamp=").append(timestamp)
                .append(", riskScore=").append(riskScore)
                .append('}')
                .toString();
    }
}
