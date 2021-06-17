//package com.mirafintech.prototype.model;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//
//@Entity
//@Table(name = "CONSUMER_FEE")
//@Getter
//@Setter
//@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ConsumerFee1 {
//
//    public enum Status {
//        PAID, NOT_PAID, CANCELED
//    }
//
//    enum Type {
//        LATE_PAYMENT_FEE // TODO: add more
//    }
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    LocalDateTime timestamp;
//
//    BigDecimal amount;
//
//    @Enumerated(EnumType.STRING)
//    private Status status;
//
//    private String additionalInfo;
//
//    private ConsumerFee(Long id, LocalDateTime timestamp, BigDecimal amount, Status status, String additionalInfo) {
//        this.id = id;
//        this.timestamp = timestamp;
//        this.amount = amount;
//        this.status = status;
//        this.additionalInfo = additionalInfo;
//    }
//
//    public ConsumerFee(LocalDateTime timestamp, BigDecimal amount, String additionalInfo) {
//        this(null, timestamp, amount, Status.NOT_PAID, additionalInfo);
//    }
//}
