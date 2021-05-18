package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "RISKLEVELS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskLevel extends EntityBase<RiskLevel> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int level;

    private String label;

    private Double lowerBound; // inclusive

    private Double upperBound; // exclusive

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Tranche tranche;

    private RiskLevel(Long id, int level, String label, Double lowerBound, Double upperBound, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.level = level;
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public RiskLevel(int level, String label, Double lowerBound, Double upperBound) {
        this(null, level, label, lowerBound, upperBound, null, null);
    }
}
