package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "TIMED_RISK_SCORE")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimedRiskScore extends EntityBase<TimedRiskScore> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "riskscore_fk")
    private RiskScore riskScore;

    private TimedRiskScore(Long id, LocalDateTime timestamp, RiskScore riskScore) {
        this.id = id;
        this.timestamp = timestamp;
        this.riskScore = riskScore;
    }

    public TimedRiskScore(LocalDateTime timestamp, RiskScore riskScore) {
        this(null, timestamp, riskScore);
    }
}
