package com.mirafintech.prototype.model.charge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.Merchant;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static com.mirafintech.prototype.model.charge.ChargeStatus.*;


@Entity
//@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class MerchantCharge extends Charge {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JsonIgnore
    protected Merchant merchant;

//    private BigDecimal fee; // TODO: if a charge is related to a loan we shouldn't have fees (late payment fee is related to a consumer, tranaction fee for merchant)

    protected MerchantCharge() {
    }

    protected MerchantCharge(Long id, LocalDateTime timestamp, Merchant merchant, ChargeStatus status) {
        super(id, timestamp, status);
        this.merchant = merchant;
    }

    @Override
    protected void verifyApplicableForChargeType(ChargeStatus status) {
        if (status == MOVED_TO_CONSUMER_BALANCE || status == MOVED_TO_LOAN_BALANCE) {
            throw new IllegalArgumentException("illegal consumer charge status: " + status);
        }
    }

    @Override
    public boolean isPending() {
        return this.status == NOT_PAID;
    }
}
