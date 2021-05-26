package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.*;
import com.mirafintech.prototype.repository.ConsumerRepository;
import com.mirafintech.prototype.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

    public Loan addLoan(LoanDto loanDto) {

        Consumer consumer = this.consumerRepository
                .findById(loanDto.getConsumerId())
                .orElseThrow(() -> new IllegalArgumentException("consumer not found: id=" + loanDto.getConsumerId()));

        Merchant merchant = this.merchantService
                .findMerchant(loanDto.getMerchantId())
                .orElseThrow(() -> new RuntimeException("merchant not found: id=" + loanDto.getMerchantId()));

        TimedRiskScore currentRiskScore = this.riskService.evaluateRiskScore(consumer);

        Loan loan = new Loan(timeService.getCurrentDateTime(), consumer, loanDto.getAmount(), currentRiskScore, merchant);
        Loan persistedLoan = this.loanRepository.saveAndFlush(loan); // TODO: check if we need to use repository if we're inside a transaction

        return persistedLoan;
    }

}
