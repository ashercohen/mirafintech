package com.mirafintech.prototype.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * configuration object sent by the driver at startup
 */
public record ConfigurationDto(LocalDateTime initTimestamp,
                               String paymentAllocationPolicy,
                               // interest, on top of tranche interest, the consumer is charge by. decimal fraction (see example below)
                               BigDecimal miraInterest,
                               Integer gracePeriodLength,
                               List<TrancheConfig> trancheConfigs) {


    public static record TrancheConfig(int lowerBoundRiskScore, // inclusive
                                       int upperBoundRiskScore, // exclusive
                                       int initialValue,        // USD
                                       BigDecimal interest) {    // as decimal for example: for 12.5% interest this value is 0.125
        public TrancheConfig {
            if (interest == null || interest.compareTo(BigDecimal.ZERO) <= 0 || interest.compareTo(BigDecimal.ONE) >= 0) {
                throw new IllegalArgumentException("illegal value for tranche interest");
            }
        }
    }
}
