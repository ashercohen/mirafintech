package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.tranche.Tranche;
import com.mirafintech.prototype.service.ConsumersService;
import com.mirafintech.prototype.service.LoanService;
import com.mirafintech.prototype.service.TimeService;
import com.mirafintech.prototype.service.TranchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/loans")
public class LoanController implements ErrorHandler {

    @Autowired
    private TranchesService tranchesService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ConsumersService consumersService;

    @Autowired
    private TimeService timeService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Loan> addLoan(@RequestBody LoanDto loan, @PathVariable long id) {

        if (loan.id() != id) {
            return ResponseEntity.badRequest().build();
        }

        this.timeService.setTime(loan.timestamp());
        Loan persistedLoan = this.loanService.processLoan(loan);
        Tranche tranche = tranchesService.allocateLoanToTranche(persistedLoan);
        Consumer consumer = this.consumersService.findById(loan.consumerId()).orElseThrow(() -> new IllegalArgumentException("consumer not found: id=" + loan.consumerId()));
        Loan updatedLoan = this.consumersService.addLoan(consumer, persistedLoan);

        // sanity
        if (!persistedLoan.equals(updatedLoan)) {
            throw new RuntimeException("persistedLoan != updatedLoan");
        }

        return ResponseEntity.of(Optional.ofNullable(persistedLoan));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Loan> findLoan(@PathVariable long id) {
        return ResponseEntity.of(this.loanService.findById(id));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {""}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Loan>> findAll() {
        return ResponseEntity.ok(this.loanService.findAll());
    }
}
