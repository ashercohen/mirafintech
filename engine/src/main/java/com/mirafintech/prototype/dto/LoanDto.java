package com.mirafintech.prototype.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record LoanDto(LocalDateTime timestamp, // string in the format: 2021-05-08T17:15:30
                      long id,
                      long consumerId,
                      BigDecimal amount,
                      long merchantId) {
}
