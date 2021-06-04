package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.model.Loan;
import com.mirafintech.prototype.model.Tranche;
import com.mirafintech.prototype.service.*;
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
    private LoansService loansService;

    @Autowired
    private ConsumersService consumersService;

    @Autowired
    private TimeService timeService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Loan> addLoan(@RequestBody LoanDto loan, @PathVariable long id) {

        if (loan.getId() != id) {
            return ResponseEntity.badRequest().build();
        }

        this.timeService.setTime(loan.getTimestamp());
        // persist loan
        Loan persistedLoan = this.loansService.processLoan(loan);

        // allocate to tranche
        Tranche tranche = tranchesService.allocateLoanToTranche(persistedLoan);

        // TODO: update consumer balance
        Consumer consumer = this.consumersService.addLoan(loan.getConsumerId(), persistedLoan);

        return ResponseEntity.of(Optional.ofNullable(persistedLoan));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Loan> getLoan(@PathVariable long id) {
        return ResponseEntity.of(this.loansService.findById(id));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {""}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Loan>> getTranches() {
        return ResponseEntity.ok(this.loansService.findAll());
    }
}
