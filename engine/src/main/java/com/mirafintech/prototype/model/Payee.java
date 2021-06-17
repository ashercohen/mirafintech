package com.mirafintech.prototype.model;

import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;


public interface Payee {

    /**
     * accept paymentAllocation
     * implementing classes should update their internal state to reflect the new payment
     */
    void accept(PaymentAllocation paymentAllocation);
}
