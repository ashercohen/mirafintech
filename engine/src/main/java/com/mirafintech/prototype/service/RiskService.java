package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.credit.DatedCreditScore;
import com.mirafintech.prototype.model.risk.RiskScore;
import com.mirafintech.prototype.model.risk.DatedRiskScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RiskService {

    @Autowired
    private TimeService timeService;

    public DatedCreditScore evaluateConsumerCreditScore(ConsumerDto consumer) {
        return new DatedCreditScore(
                consumer.getInitialCreditScore(),
                this.timeService.getCurrentDateTime()
        );
    }

    public DatedCreditScore evaluateCurrentConsumerCreditScore(Consumer consumer) {
        return new DatedCreditScore(
                consumer.currentCreditScore().getValue(),
                this.timeService.getCurrentDateTime()
        );
    }

    public DatedRiskScore evaluateRiskScore(Consumer consumer) {
        return new DatedRiskScore(
                this.timeService.getCurrentDateTime(),
                new RiskScore(consumer.currentCreditScore().getValue())
        );
    }

}
