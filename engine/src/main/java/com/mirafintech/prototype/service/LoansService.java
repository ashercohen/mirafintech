package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.*;
import com.mirafintech.prototype.repository.ConsumerRepository;
import com.mirafintech.prototype.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class LoansService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private RiskService riskService;

    /**
     * create a Loan entity and persist in the db
     * balance(s) updates etc are not performed here
     */
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Loan processLoan(LoanDto loanDto) {

        if (this.loanRepository.findById(loanDto.getId()).isPresent()) {
            throw new IllegalArgumentException("loan already exists: id=" + loanDto.getId());
        }

        Consumer consumer = this.consumerRepository
                .findById(loanDto.getConsumerId())
                .orElseThrow(() -> new IllegalArgumentException("consumer not found: id=" + loanDto.getConsumerId()));

        Merchant merchant = this.merchantService
                .findMerchant(loanDto.getMerchantId())
                .orElseThrow(() -> new RuntimeException("merchant not found: id=" + loanDto.getMerchantId()));

        Loan loan = new Loan(
                loanDto.getId(),
                timeService.getCurrentDateTime(),
                consumer,
                loanDto.getAmount(),
                this.riskService.evaluateRiskScore(consumer),
                merchant
        );

        return this.loanRepository.save(loan);
    }

    public Optional<Loan> findById(long id) {
        return this.loanRepository.findById(id);
    }

}
