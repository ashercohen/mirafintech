package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Merchant;
import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
//@DiscriminatorValue(value = "<unique value for all objects of this subclass>")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class TransactionFee extends MerchantCharge {

    @Column(name = "transaction_fee__amount")
    @Getter(AccessLevel.PROTECTED)
    private BigDecimal amount;

    protected TransactionFee() {
    }

    private TransactionFee(Long id, LocalDateTime timestamp, Merchant merchant, BigDecimal amount, ChargeStatus status) {
        super(id, timestamp, merchant, status);
        this.amount = amount;
    }
}
