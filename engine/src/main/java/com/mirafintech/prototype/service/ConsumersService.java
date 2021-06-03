package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.model.ConsumerEventLoanAdded;
import com.mirafintech.prototype.model.Loan;
import com.mirafintech.prototype.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class ConsumersService {

    @Autowired
    private ConsumerRepository repository;

    @Autowired
    private TimeService timeService;

    @Autowired
    private RiskService riskService;

    public Consumer addLoan(long consumerId, Loan loan) {

        Consumer consumer = findById(consumerId).orElseThrow(() -> new IllegalArgumentException("consumer not found: id=" + consumerId));
        // ensure consumer has this loan - won't be added again
        boolean isNewLoan = consumer.addLoan(loan);

        if (isNewLoan) {
            LocalDateTime timestamp = this.timeService.getCurrentDateTime();
            ConsumerEventLoanAdded event = ConsumerEventLoanAdded.create(loan, consumer, timestamp, "consumer service");
        }

        return consumer;
    }

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

    public Optional<Consumer> findById(long id) {
        return this.repository.findById(id);
    }
}
