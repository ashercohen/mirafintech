package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class PaymentAllocationService {

    private PaymentAllocationPolicy allocationPolicy;

    public void setAllocationPolicy(PaymentAllocationPolicy policy) {
        this.allocationPolicy = policy;
    }

    public List<PaymentAllocation> allocatePayment(Consumer consumer, BigDecimal paymentAmount, LocalDateTime timestamp) {

        if (this.allocationPolicy == null) {
            throw new RuntimeException("payment allocation policy not set");
        }

        return this.allocationPolicy.allocate(consumer, paymentAmount, timestamp);
    }
}
