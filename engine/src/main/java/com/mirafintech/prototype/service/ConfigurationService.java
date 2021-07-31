package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConfigurationDto;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * handles configuration message sent at startup
 */
@Service
public class ConfigurationService {

    @Autowired
    private TranchesService tranchesService;

    @Autowired
    private PaymentAllocationService paymentAllocationService;

    @Autowired
    private PaymentAllocationPolicyFactory paymentAllocationPolicyFactory;

    @Getter
    private BigDecimal miraInterest;

    @Getter
    private BigDecimal principleMinimumPaymentPercentage;

    @Getter
    private Integer gracePeriodLength;

    @Getter
    private BigDecimal trancheBalanceTolerance;

    // TODO: maybe get from config message
    private static final BigDecimal DEFAULT_PRINCIPLE_MINIMUM_PAYMENT_PERCENTAGE = BigDecimal.ONE.divide(new BigDecimal(36), 10, RoundingMode.HALF_UP);

    public int apply(ConfigurationDto configuration) {

        PaymentAllocationPolicy allocationPolicy = this.paymentAllocationPolicyFactory.create(configuration.paymentAllocationPolicy());
        this.paymentAllocationService.setAllocationPolicy(allocationPolicy);
        this.miraInterest = configuration.miraInterest();
        this.gracePeriodLength = Optional.ofNullable(configuration.gracePeriodLength()).orElse(14);
        this.trancheBalanceTolerance = configuration.trancheBalanceTolerance();
        this.principleMinimumPaymentPercentage = DEFAULT_PRINCIPLE_MINIMUM_PAYMENT_PERCENTAGE;

        return tranchesService.initializeTranches(configuration.trancheConfigs(), this.trancheBalanceTolerance);
    }
}
