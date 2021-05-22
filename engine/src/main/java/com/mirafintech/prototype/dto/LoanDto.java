package com.mirafintech.prototype.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class LoanDto {

    private LocalDateTime timestamp; // string in the format: 2021-05-08T17:15:30

    private long id;

    private long consumerId;

    private BigDecimal amount;

    private long merchantId;
}
