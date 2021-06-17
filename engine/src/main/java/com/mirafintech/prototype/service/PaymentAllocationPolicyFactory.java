package com.mirafintech.prototype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * TODO:
 *  find a better way to create the policy object/bean
 */
@Component
public class PaymentAllocationPolicyFactory {

    @Autowired
    DefaultPaymentAllocationPolicy defaultPaymentAllocationPolicy;

    public PaymentAllocationPolicy create(String policyName) {

        return switch (policyName) {
            case "policy1" -> null;
            case "policy2" -> null;
            default -> this.defaultPaymentAllocationPolicy;
        };
    }
}
