package com.mirafintech.prototype.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment extends EntityBase<Payment> {

    @Id
    private Long id;

    private Integer timestamp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Consumer consumer;

    private BigDecimal amount;

    @OneToMany(mappedBy = "payment", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();
}
