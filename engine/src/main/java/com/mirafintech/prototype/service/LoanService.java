package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.Merchant;
import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.repository.ConsumerRepository;
import com.mirafintech.prototype.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class LoanService {

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

    @Autowired
    private InterestCalculatingEngine interestEngine;

    public Optional<Loan> findById(long id) {
        return this.loanRepository.findById(id);
    }

    public List<Loan> findAll() {
        return this.loanRepository.findAll(Sort.by(Sort.Order.by("creationDate")).descending());
    }

    /**
     * create a Loan entity and persist in the db
     * balance(s) updates etc are not performed here
     */
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Loan processLoan(LoanDto loanDto) {

        Consumer consumer = this.consumerRepository
                .findById(loanDto.consumerId())
                .orElseThrow(() -> new IllegalArgumentException("consumer not found: id=" + loanDto.consumerId()));

        Merchant merchant = this.merchantService
                .findMerchant(loanDto.merchantId())
                .orElseThrow(() -> new RuntimeException("merchant not found: id=" + loanDto.merchantId()));

        Loan loan = new Loan(
                loanDto.id(),
                timeService.getCurrentDateTime(),
                consumer,
                loanDto.amount(),
                this.riskService.evaluateRiskScore(consumer),
                merchant
        );

        return this.loanRepository.save(loan);
    }

    public List<InterestCharge> generateInterestCharges(LocalDate date) {

        /**
         * date argument is the EndOfDay event's date
         * find all the loans that their corresponding consumer's billing cycle start
         * day matches the following day of date argument.
         *
         * For example:
         * if EndOfDay event date is 10/06/2021, we need to find all the loans that their cycle start day is 11.
         */
        LocalDateTime tomorrowMidnight = date.plusDays(1).atTime(LocalTime.MIDNIGHT);

        /**
         * steps:
         * 1. iterate through all active loans that their cycle ends today
         * 2. for each loan that it's cycle completes today:
         * 3. calculate interest
         * 4. generate charge
         */
        return findAllWithBillingCycleEnd(tomorrowMidnight)
                .map(loan -> {
                    InterestCharge interestCharge = new InterestCharge(tomorrowMidnight, loan, this.interestEngine.calculate(loan, loan.getConsumer()));
                    loan.addLoanCharge(interestCharge);
                    return interestCharge;
                })
                .toList();
    }

    /**
     * find all loans that billing cycle ends and interest charge should be generated
     * - loan is active
     * - loan cycle start day = dateTime.dayOfMonth
     * - loan is older than 1 month - interest is not charged in first month/cycle
     */
    private Stream<Loan> findAllWithBillingCycleEnd(LocalDateTime dateTime) {

        return this.loanRepository.findAll()
                .stream()
                .filter(Loan::isActive)
                .filter(loan -> loan.getConsumer().getBillingCycleStartDay() == dateTime.getDayOfMonth())
                .filter(loan -> loan.getCreationDate().plusMonths(1L).isBefore(dateTime));
    }
}
