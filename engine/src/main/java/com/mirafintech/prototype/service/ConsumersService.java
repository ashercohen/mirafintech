package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConsumersService {

    @Autowired
    private ConsumerRepository repository;

    @Autowired
    private TimeService timeService;

    @Autowired
    private RiskService riskService;

    public Consumer addConsumer(ConsumerDto consumerDto) {

        if (this.repository.findById(consumerDto.getId()).isPresent()) {
            throw new IllegalArgumentException("consumer already exists: id=" + consumerDto.getId());
        }

        Consumer consumer = new Consumer(
                consumerDto,
                this.riskService.evaluateConsumerCreditScore(consumerDto),
                this.timeService.getCurrentDateTime());

        // persist consumer
        return this.repository.saveAndFlush(consumer);
    }
}
