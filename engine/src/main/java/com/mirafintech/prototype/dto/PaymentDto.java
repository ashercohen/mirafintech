package com.mirafintech.prototype.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * payment object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {

    private long id; // mapped to payment externalId

    private LocalDateTime timestamp; // string in the format: 2021-05-08T17:15:30

    private long consumerId;

    private BigDecimal amount;
}
