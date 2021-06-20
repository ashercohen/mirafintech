package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConfigurationDto;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public int apply(ConfigurationDto configuration) {

        PaymentAllocationPolicy allocationPolicy = this.paymentAllocationPolicyFactory.create(configuration.paymentAllocationPolicy());
        this.paymentAllocationService.setAllocationPolicy(allocationPolicy);
        this.miraInterest = configuration.miraInterest();

        return tranchesService.initializeTranches(configuration.trancheConfigs());
    }
}
