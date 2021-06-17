package com.mirafintech.prototype.model.credit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "DATED_CREDIT_SCORE")
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatedCreditScore {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int value;

    private LocalDateTime timestamp;

    private DatedCreditScore(Long id, int value, LocalDateTime timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    public DatedCreditScore(int value, LocalDateTime timestamp) {
        this(null, value, timestamp);
    }

    public DatedCreditScore withTimestamp(LocalDateTime newTimestamp) {
        return new DatedCreditScore(null, this.value, newTimestamp);
    }
}
