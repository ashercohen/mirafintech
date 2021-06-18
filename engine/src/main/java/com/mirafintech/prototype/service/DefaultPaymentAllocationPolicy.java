package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.charge.InterestCharge;
import com.mirafintech.prototype.model.charge.LatePaymentFee;
import com.mirafintech.prototype.model.charge.LoanFee;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.model.loan.Loan;
import com.mirafintech.prototype.model.payment.allocation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


@Service
public class DefaultPaymentAllocationPolicy implements PaymentAllocationPolicy {

    @Autowired
    private InterestCalculatingEngine interestEngine;

    @Autowired
    private TimeService timeService;

    private record AllocationTarget(AllocationType type,
                                    Loan loan,
                                    LocalDateTime loanDate,
//                                    List<? extends Charge> charges,
                                    List<LoanFee> loanFees,
                                    List<InterestCharge> interestCharges,
                                    Optional<BigDecimal> principleAmount
                                    /*
                                    BigDecimal interest,
                                    List<DatedBalance> balanceHistory*/) {}

    private record Allocation(List<? extends PaymentAllocation> allocations, BigDecimal totalAllocated) {
        static final Allocation empty = new Allocation(List.of(), BigDecimal.ZERO);
    }

//    need to test payment when loan already have interest charge

    /**
     * The default behavior for charge allocations would be:
     * - By payment type: Priority 1 – Fees, priority 2 – Interest, priority 3 – principle
     * - Inside each type by loan date, older loans get priority over newer loans.
     * - For interest charges, Mira and Tranche's portions have the same priority.
     */
    @Override
    public List<PaymentAllocation> allocate(Consumer consumer, BigDecimal paymentAmount, LocalDateTime timestamp) {

        // TODO: if/when we'll have multiple types of consumer charges we might need to split
        //  this into two lists (maybe payment priority would be different)

        /**
         * collect payment targets: fees (consumer+loan), interest charges and principle
         */
        // TODO: remove this fake fee
        List<LatePaymentFee> latePaymentFees = List.of(new LatePaymentFee(timestamp, consumer, BigDecimal.valueOf(7)));//  consumer.unpaidFees();
        List<AllocationTarget> allocationTargets = collectLoansAllocationTargets(consumer);

        /**
         * consumer fees
         */
        BigDecimal budget = copyOf(paymentAmount);
        Allocation feesAllocations = allocateToConsumerFees(consumer, latePaymentFees, budget, timestamp);
        /**
         * loan fees
         */
        budget = budget.subtract(feesAllocations.totalAllocated());
        Allocation loanFeesAllocations = allocateToLoansFees(allocationTargets, budget, timestamp);
        /**
         * interest
         */
        budget = budget.subtract(loanFeesAllocations.totalAllocated());
        Allocation interestAllocations = allocateToLoansInterest(allocationTargets, budget, timestamp);
        /**
         * principle
         */
        budget = budget.subtract(interestAllocations.totalAllocated());
        Allocation principleAllocations = allocateToLoansPrinciple(allocationTargets, budget, timestamp);

        return Stream.of(feesAllocations, loanFeesAllocations, interestAllocations, principleAllocations)
                .flatMap(allocation -> allocation.allocations().stream())
                .map(c -> (PaymentAllocation) c)
                .toList();
    }



    private List<AllocationTarget> collectLoansAllocationTargets(Consumer consumer) {

        return consumer.getLoans()
                .stream()
                .flatMap(loan ->
                    Stream.of(
                            new AllocationTarget(
                                    AllocationType.LOAN_FEE,
                                    loan,
                                    loan.getCreationDate(),
                                    loan.getUnpaidLoanFees(),
                                    List.of(),
                                    Optional.empty()
                            ),
                            new AllocationTarget(
                                    AllocationType.TRANCHE_INTEREST,
                                    loan,
                                    loan.getCreationDate(),
                                    List.of(),
                                    loan.getUnpaidInterestCharges(),
                                    Optional.empty()
                            ),
//                            new AllocationTarget(AllocationType.MIRA_INTEREST, loan, loan.getCreationDate(), <call loan method that returns mira fees>, Optional.empty()),
                            new AllocationTarget(
                                    AllocationType.PRINCIPLE,
                                    loan,
                                    loan.getCreationDate(),
                                    List.of(),
                                    List.<InterestCharge>of(), //TODO: replace with call to get the list from the loan
                                    Optional.of(loan.currentBalance())
                            )
                    )
                )
                .sorted(Comparator.comparing(AllocationTarget::loanDate))
                .toList();
    }

    private Allocation allocateToConsumerFees(Consumer consumer, List<LatePaymentFee> latePaymentFees, final BigDecimal budget, LocalDateTime timestamp) {

        if (budget.compareTo(BigDecimal.ZERO) <= 0 || latePaymentFees.isEmpty()) {
            return Allocation.empty;
        }

        List<LatePaymentFee> sortedFees = latePaymentFees.stream()
                .sorted(Comparator.comparing(LatePaymentFee::getTimestamp))
                .toList();

        BigDecimal budgetBalance = copyOf(budget);
        List<LatePaymentFeePaymentAllocation> allocations = new ArrayList<>(latePaymentFees.size());

        for (LatePaymentFee fee : sortedFees) {

            BigDecimal allocationAmount = calculateAllocationAmount(budgetBalance, fee.balance());

            LatePaymentFeePaymentAllocation paymentAllocation = new LatePaymentFeePaymentAllocation(timestamp, consumer, fee, allocationAmount);
            // TODO: check if needed to add PA to fee here: maybe ConsumerCharges are different than LoanCharges
//            fee.addPaymentAllocation(paymentAllocation);
            allocations.add(paymentAllocation);
            budgetBalance = budgetBalance.subtract(allocationAmount);
        }

        return new Allocation(allocations, budget.subtract(budgetBalance));
    }

    private Allocation allocateToLoansFees(List<AllocationTarget> allocationTargets, BigDecimal budget, LocalDateTime timestamp) {

        if (budget.compareTo(BigDecimal.ZERO) <= 0) {
            return Allocation.empty;
        }

        // filter and sort
        List<AllocationTarget> loanFeeTargets = allocationTargets.stream()
                .filter(target -> target.type() == AllocationType.LOAN_FEE)
                .filter(Predicate.not(target -> target.loanFees().isEmpty()))
                .sorted(Comparator.comparing(AllocationTarget::loanDate))
                .toList();

        BigDecimal budgetBalance = copyOf(budget);
        List<LoanFeePaymentAllocation> allocations = new ArrayList<>(loanFeeTargets.size());

        for (AllocationTarget target : loanFeeTargets) {

            for (LoanFee loanFee : target.loanFees()) {

                BigDecimal allocationAmount = calculateAllocationAmount(budgetBalance, loanFee.balance());
                LoanFeePaymentAllocation paymentAllocation = new LoanFeePaymentAllocation(timestamp, target.loan(), loanFee, allocationAmount);
                allocations.add(paymentAllocation);
                budgetBalance = budgetBalance.subtract(allocationAmount);
            }
        }

        return new Allocation(allocations, budget.subtract(budgetBalance));
    }

    private Allocation allocateToLoansInterest(List<AllocationTarget> allocationTargets, BigDecimal budget, LocalDateTime timestamp) {

        if (budget.compareTo(BigDecimal.ZERO) <= 0) {
            return Allocation.empty;
        }

        // filter and sort
        List<AllocationTarget> interestTargets = allocationTargets.stream()
                .filter(target -> target.type() == AllocationType.TRANCHE_INTEREST || target.type() == AllocationType.MIRA_INTEREST)
                .filter(Predicate.not(target -> target.interestCharges().isEmpty()))
                .sorted(Comparator.comparing(AllocationTarget::loanDate))
                .toList();

        BigDecimal budgetBalance = copyOf(budget);
        List<InterestPaymentAllocation> allocations = new ArrayList<>(interestTargets.size());

        for (AllocationTarget target : interestTargets) {

            for (InterestCharge interestCharge : target.interestCharges()) {
                BigDecimal interestBalance = interestCharge.balance(); // .getAmount();

                BigDecimal allocationAmount = calculateAllocationAmount(budgetBalance, interestBalance);
                // TODO: split mira/tranche interest
                InterestPaymentAllocation paymentAllocation = new InterestPaymentAllocation(timestamp, target.loan(), interestCharge, allocationAmount, BigDecimal.ZERO);
                allocations.add(paymentAllocation);
                budgetBalance = budgetBalance.subtract(allocationAmount);
            }
        }

        return new Allocation(allocations, budget.subtract(budgetBalance));
    }

    private Allocation allocateToLoansPrinciple(List<AllocationTarget> allocationTargets, BigDecimal budget, LocalDateTime timestamp) {

        if (budget.compareTo(BigDecimal.ZERO) <= 0) {
            return Allocation.empty;
        }

        // filter and sort
        List<AllocationTarget> principleTargets = allocationTargets.stream()
                .filter(target -> target.type() == AllocationType.PRINCIPLE)
                .sorted(Comparator.comparing(AllocationTarget::loanDate))
                .toList();

        BigDecimal budgetBalance = copyOf(budget);
        List<PrinciplePaymentAllocation> allocations = new ArrayList<>(principleTargets.size());

        for (AllocationTarget principleTarget : principleTargets) {
            BigDecimal principle = principleTarget.principleAmount().orElse(BigDecimal.ZERO);
            BigDecimal allocationAmount = calculateAllocationAmount(budgetBalance, principle);
            allocations.add(new PrinciplePaymentAllocation(timestamp, principleTarget.loan(), allocationAmount, principle));
            budgetBalance = budgetBalance.subtract(allocationAmount);
        }

        return new Allocation(allocations, budget.subtract(budgetBalance));
    }

    /**
     * calculate allocation amount given budget and request
     * 1. full allocation:
     *    - if budget >= request => allocate entire request
     * 2. partial allocation:
     *    - if budget < request  => allocate entire budget
     */
    private BigDecimal calculateAllocationAmount(BigDecimal budget, BigDecimal request) {
        return budget.compareTo(request) >= 0 ? request : budget;
    }

    private BigDecimal copyOf(BigDecimal value) {

        if (value == null || value.getClass() != BigDecimal.class) {
            throw new IllegalArgumentException("value is either null or not BigDecimal");
        }

        return new BigDecimal(value.toString());
    }
}
