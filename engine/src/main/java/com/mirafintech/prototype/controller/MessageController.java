package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.dto.MerchantDto;
import com.mirafintech.prototype.model.*;
import com.mirafintech.prototype.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/")
public class MessageController {

    @Autowired
    private TranchesService tranchesService;

    @Autowired
    private LoansService loansService;

    @Autowired
    private ConsumersService consumersService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UCITransactionService uciTransactionService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"loans/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Loan> addLoan(@RequestBody LoanDto loan, @PathVariable long id) {

        if (loan.getId() != id) {
            return ResponseEntity.badRequest().build();
        }
        this.timeService.setTime(loan.getTimestamp());
        // persist loan
        Loan persistedLoan = this.loansService.addLoan(loan);

        // allocate to tranche
        Tranche tranche = tranchesService.allocateLoanToTranche(persistedLoan);

        return ResponseEntity.of(Optional.ofNullable(persistedLoan));
    }

    @Transactional(readOnly = false)
    @RequestMapping(path = {"consumers/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Consumer> addConsumer(@RequestBody ConsumerDto consumer, @PathVariable long id) {

        if (consumer.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Consumer savedEntity = this.consumersService.addConsumer(consumer);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }

    @Transactional(readOnly = false)
    @RequestMapping(path = {"merchants/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Merchant> addMerchant(@RequestBody MerchantDto merchant, @PathVariable long id) {

        if (merchant.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Merchant savedEntity = this.merchantService.addMerchant(merchant);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }

    @Transactional(readOnly = false)
    @RequestMapping(path = {"set/time"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LocalDateTime> setTime(@RequestBody String dateTime) {

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        SystemTime systemTime = this.timeService.setTime(localDateTime);

        return ResponseEntity.of(Optional.ofNullable(systemTime.getDateTime()));
    }

    @Transactional(readOnly = false)
    @RequestMapping(path = {"set/config"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> setConfiguration(@RequestBody ConfigurationDto configuration) {

        this.timeService.setTime(configuration.getInitTimestamp());
        int numTranches = this.configurationService.apply(configuration);

        return ResponseEntity.ok().body(String.format("initialized %d tranches", numTranches));
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

    /**
     * custom exception handler for endpoints that define request body validation
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleException(Exception e) {

        e.printStackTrace();

        if (e instanceof IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        }

        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
