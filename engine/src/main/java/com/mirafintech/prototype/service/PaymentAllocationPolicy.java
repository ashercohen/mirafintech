package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public interface PaymentAllocationPolicy { // TODO: can use sealed interface

    enum AllocationType {
        /*TRANSACTION_FEE, */ // merchant related
        LATE_PAYMENT_FEE, LOAN_FEE, TRANCHE_INTEREST, MIRA_INTEREST, PRINCIPLE
    }

    List<PaymentAllocation> allocate(Consumer consumer, BigDecimal paymentAmount, LocalDateTime timestamp);
}
