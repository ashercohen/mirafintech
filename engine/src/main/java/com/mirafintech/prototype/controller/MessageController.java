package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.model.Loan;
import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.model.UCICreditCard;
import com.mirafintech.prototype.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/")
public class MessageController {

    @Autowired
    private LoansService loansService;

    @Autowired
    private ConsumersService consumersService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UCITransactionService uciTransactionService;

    @RequestMapping(path = {"loans/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Loan> addLoan(@RequestBody LoanDto loan, @PathVariable long id) {

        if (loan.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Loan savedLoan = this.loansService.addLoan(loan);

        return ResponseEntity.of(Optional.ofNullable(savedLoan));
    }

    @RequestMapping(path = {"consumers/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Consumer> addConsumer(@RequestBody ConsumerDto consumer, @PathVariable long id) {

        if (consumer.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Consumer savedEntity = this.consumersService.addConsumer(consumer);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }

    @RequestMapping(path = {"set/time"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LocalDateTime> setTime(@RequestBody String dateTime) {

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        SystemTime systemTime = this.timeService.setTime(localDateTime);

        return ResponseEntity.of(Optional.ofNullable(systemTime.getDateTime()));
    }

    @RequestMapping(path = {"set/config"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> setConfiguration(@RequestBody ConfigurationDto configuration) {

        this.configurationService.apply(configuration);

        return ResponseEntity.ok(null);
    }

    /**
     * this endpoint is for testing only - loads a row of the UCI dataset into the database
     */
    @RequestMapping(path = {"uci/write/"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UCICreditCard> acceptTransaction(@RequestBody String csv) {

        UCICreditCard uciCreditCard = new UCICreditCard(csv);
        UCICreditCard savedEntity = this.uciTransactionService.writeUCICreditCard(uciCreditCard);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }

}
