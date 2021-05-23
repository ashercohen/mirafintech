package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.model.RiskScore;
import com.mirafintech.prototype.model.TimedCreditScore;
import com.mirafintech.prototype.model.TimedRiskScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RiskService {

    @Autowired
    private TimeService timeService;

    public TimedCreditScore evaluateConsumerCreditScore(ConsumerDto consumer) {
        return new TimedCreditScore(
                consumer.getInitialCreditScore(),
                this.timeService.getCurrentDateTime()
        );
    }

    public TimedCreditScore evaluateCurrentConsumerCreditScore(Consumer consumer) {
        return new TimedCreditScore(
                consumer.currentCreditScore().getValue(),
                this.timeService.getCurrentDateTime()
        );
    }

    public TimedRiskScore evaluateRiskScore(Consumer consumer) {
        return new TimedRiskScore(
                this.timeService.getCurrentDateTime(),
                new RiskScore(consumer.currentCreditScore().getValue())
        );
    }

}
