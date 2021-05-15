package com.mirafintech.prototype.dto;

import lombok.Data;
import java.math.BigDecimal;


@Data
public class LoanDto {

    private long id;

    private long consumerId;

    private BigDecimal amount;
}
