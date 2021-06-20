package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.PaymentDto;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.model.payment.allocation.PaymentAllocation;
import com.mirafintech.prototype.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ConsumersService consumersService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private PaymentAllocationService paymentAllocationService;


    public Optional<Payment> findById(long id) {
        return this.paymentRepository.findById(id);
    }

    public List<Payment> findAll() {
        return this.paymentRepository.findAll(Sort.by(Sort.Order.by("timestamp")).descending());
    }

    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Payment processConsumerPayment(PaymentDto paymentDto) {

        LocalDateTime timestamp = this.timeService.getCurrentDateTime();

        //throw new IllegalArgumentException("consumer already exists: id=" + consumerDto.getId());
        findByExternalId(paymentDto.getId())
                .ifPresent(payment -> {
                    throw new IllegalArgumentException("payment already submitted: id=" + paymentDto.getId());
                });

        Consumer consumer = consumersService.findById(paymentDto.getConsumerId())
                .orElseThrow(() -> new IllegalArgumentException("consumer not found: id=" + paymentDto.getConsumerId()));
        this.consumersService.verifyPaymentApplicable(consumer, paymentDto.getAmount());

        // allocatePayment to loans/interest/principle/unpaidFees/charges/....
        List<PaymentAllocation> paymentAllocations = this.paymentAllocationService.allocatePayment(consumer, paymentDto.getAmount(), timestamp);

        for (PaymentAllocation allocation : paymentAllocations) {
            allocation.getPayee().accept(allocation);
        }

        Payment payment = Payment.create(paymentDto.getId(), timestamp, consumer, paymentDto.getAmount(), paymentAllocations);

        return this.paymentRepository.save(payment);
    }

    public Optional<Payment> findByExternalId(Long externalId) {
        return this.paymentRepository.findByExternalId(externalId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PaymentService{");
        sb.append("paymentRepository=").append("PaymentRepository");
        sb.append(", consumersService=").append("ConsumersService");
        sb.append(", timeService=").append("TimeService");
        sb.append(", paymentAllocationService=").append("PaymentAllocationService");
        sb.append('}');
        return sb.toString();
    }
}
