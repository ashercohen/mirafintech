package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class ConsumersService {

    @Autowired
    private ConsumerRepository repository;

    @Autowired
    private TimeService timeService;

    @Autowired
    private RiskService riskService;

    private static final List<Integer> BILLING_CYCLE_START_DAYS = List.of(1, 8, 15, 22);


    public Loan addLoan(Consumer consumer, Loan loan) {

        // ensure consumer has this loan
        if (consumer.isLoanAlreadyExists(loan)) {
            throw new RuntimeException("consumer already has a loan with externalId=" + loan.getExternalId());
        }

        boolean added = consumer.addLoan(loan, this.timeService.getCurrentDateTime());

        // sanity - should not get inside
        if (!added) {
            throw new RuntimeException("loan weren't added to consumer: externalId=" + loan.getExternalId());
        }

        return loan;
    }

    public Consumer addConsumer(ConsumerDto consumerDto) {

        if (this.repository.findById(consumerDto.id()).isPresent()) {
            throw new IllegalArgumentException("consumer already exists: id=" + consumerDto.id());
        }

        Consumer consumer = new Consumer(
                consumerDto,
                this.riskService.evaluateConsumerCreditScore(consumerDto),
                allocateBillingCycleStartDayOfMonth(consumerDto.id()),
                this.timeService.getCurrentDateTime()
        );

        // persist consumer
        return this.repository.saveAndFlush(consumer);
    }

    public Optional<Consumer> findById(long id) {
        return this.repository.findById(id);
    }

    public void verifyPaymentApplicable(Consumer consumer, BigDecimal paymentAmount) {

        /**
         * TODO:
         *  this might change when we allow a user to keep a positive balance
         */
        if (paymentAmount.compareTo(consumer.getBalance().abs()) > 0) {
          //  throw new RuntimeException("consumer balance is smaller than payment");
        }
    }

    private static int allocateBillingCycleStartDayOfMonth(long consumerId) {

        int idx = (int)consumerId % BILLING_CYCLE_START_DAYS.size();
        return BILLING_CYCLE_START_DAYS.get(idx);
    }
}
