package com.mirafintech.prototype.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * configuration object sent by the driver at startup
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationDto {

    private List<RiskLevel> riskLevels;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RiskLevel {
        private int level;
        private String label;
        private Double lowerBound; // inclusive
        private Double upperBound; // exclusive
    }
}
