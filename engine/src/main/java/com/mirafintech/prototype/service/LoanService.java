package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.LoanDto;
import com.mirafintech.prototype.model.Merchant;
import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.charge.LoanFee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.consumer.event.MinimumPaymentConsumerEvent;
import com.mirafintech.prototype.model.interest.RawInterval;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.PrincipleMinimumPayment;
import com.mirafintech.prototype.repository.ConsumerRepository;
import com.mirafintech.prototype.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private ConfigurationService configurationService;

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

    public List<MinimumPaymentConsumerEvent> generateMinimumPaymentNotifications(LocalDate date) {

        /**
         * this record used inside this method only for collecting all components
         * that are required for generating minimum payment event
         */
        record MinimumPaymentBreakdown(List<InterestCharge> interestCharges,
                                       List<LoanFee> unpaidLoanFees,
                                       List<PrincipleMinimumPayment> principleMinimumPayments) {

            public MinimumPaymentBreakdown merge(MinimumPaymentBreakdown other) {
                return new MinimumPaymentBreakdown(
                        Stream.concat(this.interestCharges().stream(), other.interestCharges().stream()).toList(),
                        Stream.concat(this.unpaidLoanFees().stream(), other.unpaidLoanFees().stream()).toList(),
                        Stream.concat(this.principleMinimumPayments().stream(), other.principleMinimumPayments().stream()).toList()
                );
            }
        }

        /**
         * date argument is the EndOfDay event's date
         * find all the loans that their corresponding consumer's billing cycle start
         * day matches the following day of date argument.
         *
         * For example:
         * if EndOfDay event date is 10/06/2021, we need to find all the loans that their cycle start day is 11.
         */
        LocalDateTime tomorrowMidnight = date.plusDays(1).atTime(LocalTime.MIDNIGHT);
        RawInterval interestCalculationInterval = new RawInterval(tomorrowMidnight.toLocalDate().minusMonths(1), tomorrowMidnight.toLocalDate());
        final List<Loan> loansWithBillingCycleEnd = findAllWithBillingCycleEnd(tomorrowMidnight);

        return loansWithBillingCycleEnd.stream()
                .map(Loan::getConsumer)
                .map(consumer ->
                        consumer.getLoans().stream()
                                .filter(loansWithBillingCycleEnd::contains)
                                .map(loan ->
                                        new MinimumPaymentBreakdown(
                                                List.of(new InterestCharge(tomorrowMidnight, loan, this.interestEngine.calculate(loan, interestCalculationInterval))),
                                                loan.getUnpaidLoanFees(),
                                                List.of(new PrincipleMinimumPayment(loan, getMinimumPrinciplePayments(loan.currentBalance())))))
                                .reduce(MinimumPaymentBreakdown::merge)
                                .map(minimumPaymentBreakdown ->
                                        new MinimumPaymentConsumerEvent(
                                                tomorrowMidnight,
                                                consumer,
                                                "billing_cycle_end",
                                                tomorrowMidnight.plusDays(this.configurationService.getGracePeriodLength()).toLocalDate(),
                                                minimumPaymentBreakdown.interestCharges(),
                                                minimumPaymentBreakdown.unpaidLoanFees(),
                                                minimumPaymentBreakdown.principleMinimumPayments())
                                )
                                .orElseThrow(() -> new RuntimeException("could not generate minimum payment consumer event: consumerId=" + consumer.getId()))
                )
                .toList();
    }

    /**
     * find all loans that billing cycle ends and interest charge should be generated
     * - loan is active
     * - loan cycle start day = dateTime.dayOfMonth
     * - loan is older than 1 month - interest is not charged in first month/cycle
     */
    private List<Loan> findAllWithBillingCycleEnd(LocalDateTime dateTime) {

        return this.loanRepository.findAll()
                .stream()
                .filter(Loan::isActive)
                .filter(loan -> loan.getConsumer().getBillingCycleStartDay() == dateTime.getDayOfMonth())
                // TODO: this filter must be removed since we do want "fresh" loans to have minimum payment, we just don't want them to include interest (in the first month)
//                .filter(loan -> loan.getCreationDate().plusMonths(1L).isBefore(dateTime))
                .toList();
    }

    private BigDecimal getMinimumPrinciplePayments(BigDecimal loanBalance) {
        return loanBalance.multiply(this.configurationService.getPrincipleMinimumPaymentPercentage());
    }

    // TODO: not used anymore. we generate minimum payment consumer event that contains interest charges
//    public List<InterestCharge> generateInterestCharges(LocalDate date) {
//
//        LocalDateTime tomorrowMidnight = date.plusDays(1).atTime(LocalTime.MIDNIGHT);
//
//        /**
//         * steps:
//         * 1. iterate through all active loans that their cycle ends today
//         * 2. for each loan that it's cycle completes today:
//         * 3. calculate interest
//         * 4. generate charge
//         */
//        return findAllWithBillingCycleEnd(tomorrowMidnight)
//                .stream()
//                .map(loan -> {
//                    InterestCharge interestCharge = new InterestCharge(tomorrowMidnight, loan, this.interestEngine.calculate(loan, loan.getConsumer()));
//                    loan.addLoanCharge(interestCharge);
//                    return interestCharge;
//                })
//                .toList();
//    }
}
