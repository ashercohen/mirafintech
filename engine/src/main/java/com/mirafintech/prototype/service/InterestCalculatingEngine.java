package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class InterestCalculatingEngine {

    @Autowired
    private ConfigurationService configurationService;

    public BigDecimal calculate(Loan loan, Consumer consumer) {

        BigDecimal miraInterest = configurationService.getMiraInterest();

        // TODO: implement
//        throw new RuntimeException("not implemented yet");
        return BigDecimal.valueOf(10L);
    }

}
