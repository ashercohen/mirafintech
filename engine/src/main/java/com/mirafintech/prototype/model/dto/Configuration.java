package com.mirafintech.prototype.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * configuration object sent by the driver at startup
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Configuration {

    private List<RiskLevel> riskLevels;

    @Data
    @AllArgsConstructor
    public static class RiskLevel {
        private int level;
        private String label;
        private Double lowerBound; // inclusive
        private Double upperBound; // exclusive
    }
}
