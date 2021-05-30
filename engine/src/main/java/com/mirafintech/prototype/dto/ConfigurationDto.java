package com.mirafintech.prototype.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


/**
 * configuration object sent by the driver at startup
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationDto {

    private LocalDateTime initTimestamp;

    private List<TrancheConfig> trancheConfigs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrancheConfig {
        private int lowerBoundRiskScore; // inclusive
        private int upperBoundRiskScore; // exclusive
        private int initialValue; // USD
    }
}
