package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "TIMED_CREDIT_SCORE")
@Getter
//@Setter
//@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimedCreditScore {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Setter(value = AccessLevel.PRIVATE)
    private int value;

//    @Setter(value = AccessLevel.PRIVATE)
    private LocalDateTime timestamp;

    private TimedCreditScore(Long id, int value, LocalDateTime timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    public TimedCreditScore(int value, LocalDateTime timestamp) {
        this(null, value, timestamp);
    }

    public TimedCreditScore withTimestamp(LocalDateTime newTimestamp) {
        return new TimedCreditScore(null, this.value, newTimestamp);
    }
}
