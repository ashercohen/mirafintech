package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.HasAmount;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "CHARGE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract /*sealed*/ class Charge implements HasAmount
        /*permits ConsumerCharge, LoanCharge, MerchantCharge */{
//public abstract /*sealed*/ class Charge
//        /*permits ConsumerCharge, LoanCharge, MerchantCharge*/ {

    abstract public /*protected*/ BigDecimal getAmount();

    //TODO: do we need this method??
//    //    need to implement in derived classes
//    abstract public void updateAmount();

    abstract public boolean isPending();

    abstract protected void verifyApplicableForChargeType(ChargeStatus status);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected LocalDateTime timestamp;

    @Setter
    @Enumerated(EnumType.STRING)
    protected ChargeStatus status;

    protected Charge() {
    }

    protected Charge(Long id, LocalDateTime timestamp, ChargeStatus status) {
        verifyApplicableForChargeType(status);
        this.id = id;
        this.timestamp = timestamp;
        this.status = status;
    }
}
