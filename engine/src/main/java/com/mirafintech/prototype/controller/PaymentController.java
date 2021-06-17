package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.PaymentDto;
import com.mirafintech.prototype.model.payment.Payment;
import com.mirafintech.prototype.service.PaymentService;
import com.mirafintech.prototype.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/payments")
public class PaymentController implements ErrorHandler {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TimeService timeService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Payment> addPayment(@RequestBody PaymentDto paymentDto, @PathVariable long id) {

        if (paymentDto.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }

        this.timeService.setTime(paymentDto.getTimestamp());

        Payment payment = this.paymentService.processConsumerPayment(paymentDto);

        return ResponseEntity.of(Optional.ofNullable(payment));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Payment> findPayment(@PathVariable long id) {
        return ResponseEntity.of(this.paymentService.findById(id));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {""}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Payment>> findAll() {
        return ResponseEntity.ok(this.paymentService.findAll());
    }
}
