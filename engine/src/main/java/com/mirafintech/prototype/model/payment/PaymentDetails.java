package com.mirafintech.prototype.model.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record PaymentDetails(Long id, Long externalId, LocalDateTime timestamp, Long consumerId, BigDecimal amount, List<AllocationDetails> allocationDetails) {}

record AllocationDetails(Long id, String type) {}
