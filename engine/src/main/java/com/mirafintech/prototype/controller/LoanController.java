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

        if (loan.getId() != id) {
            return ResponseEntity.badRequest().build();
        }

        this.timeService.setTime(loan.getTimestamp());

        // persist loan
        Loan persistedLoan = this.loanService.processLoan(loan);

        // allocate to tranche
        Tranche tranche = tranchesService.allocateLoanToTranche(persistedLoan);

        // TODO: update consumer balance
        Consumer consumer = this.consumersService.addLoan(loan.getConsumerId(), persistedLoan);

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
