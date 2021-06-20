package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "DATED_BALANCE")
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatedBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    private BigDecimal balance;

    private DatedBalance(Long id, LocalDateTime timestamp, BigDecimal balance) {
        this.id = id;
        this.timestamp = timestamp;
        this.balance = balance;
    }

    public DatedBalance(LocalDateTime timestamp, BigDecimal balance) {
        this(null, timestamp, balance);
    }
}
