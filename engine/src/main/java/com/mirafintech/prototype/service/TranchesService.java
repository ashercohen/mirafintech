package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.model.*;
import com.mirafintech.prototype.repository.TrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;


@Service
public class TranchesService {

    @Autowired
    private TrancheRepository repository;

    @Autowired
    private TimeService timeService;

    private List<Tranche> tranches = new ArrayList<>();

    /**
     * allocate transaction to a tranche incl.:
     * - find a suitable tranche - created if needed
     * - assign loan to tranche
     * - record operation as tranche event
     * - update tranche balance
     *
     * this method must be called in the context of an existing transaction
     */
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Tranche allocateLoanToTranche(Loan loan) {

        LocalDateTime timestamp = this.timeService.getCurrentDateTime();
        Tranche tranche = findTranche(loan.currentRiskScore(), loan.getAmount());
        doAllocateLoanToTranche(loan, tranche, timestamp);

        return tranche;
    }

    public int initializeTranches(List<ConfigurationDto.TrancheConfig> trancheConfigs) {

        LocalDateTime timestamp = timeService.getCurrentDateTime();
        trancheConfigs.sort(Comparator.comparing(ConfigurationDto.TrancheConfig::getLowerBoundRiskScore));

        List<Tranche> added = IntStream.range(0, trancheConfigs.size())
                .boxed()
                .map(i -> {
                    ConfigurationDto.TrancheConfig config = trancheConfigs.get(i);
                    return Tranche.createEmptyTranche(
                            new BigDecimal(config.getInitialValue()),
                            timestamp,
                            i,
                            new RiskScore(config.getLowerBoundRiskScore()),
                            new RiskScore(config.getUpperBoundRiskScore()));
                })
                .map(this::persistTranche)
                .toList();


        if (added.size() != trancheConfigs.size()) {
            throw new RuntimeException(String.format("failed to initialize tranches: requested=%d, actual=%d", trancheConfigs.size(), added.size()));
        }

        return added.size();
    }

    private Tranche persistTranche(Tranche tranche) {
        Tranche persistedTranche = this.repository.save(tranche);
        this.tranches.add(persistedTranche);

        return persistedTranche;
    }

    /**
     * find a tranche with suitable risk level and balance
     * create new tranche if none found
     */
    private Tranche findTranche(DatedRiskScore loanRiskScore, BigDecimal amount) {

        List<Tranche> matchingTranches =
                this.tranches.stream()
                        .filter(tranche -> tranche.getRiskLevel().contains(loanRiskScore.getRiskScore()))
                        .filter(tranche -> tranche.currentBalance().compareTo(amount) >= 0)
                        .toList();

        return switch (matchingTranches.size()) {
            case 0 -> {
                // no matching tranche - allocate new tranche
                Tranche tranche = findBy(loanRiskScore.getRiskScore());
                // returns attached entity
                yield allocateNewTrancheLike(tranche);
            }

            // one matching tranche - return it
            case 1 -> this.repository.findById(matchingTranches.get(0).getId()).orElseThrow();

            // multiple matches - return any // TODO: selecting one of many matching tranches requires additional logic
            default -> this.repository.findById(matchingTranches.get(0).getId()).orElseThrow();
        };
    }

    /**
     * create a new empty tranche similar to the provided one in terms of initial value and risk level
     */
    private Tranche allocateNewTrancheLike(Tranche tranche) {

        Tranche newTranche =
                Tranche.createEmptyTranche(
                        tranche.getInitialValue(),
                        this.timeService.getCurrentDateTime(),
                        tranche.getRiskLevel().getId(),
                        tranche.getRiskLevel().getLowerBound(),
                        tranche.getRiskLevel().getUpperBound());

        return persistTranche(newTranche);
    }

    private Tranche findBy(RiskScore riskScore) {
        return this.tranches.stream()
                .filter(tranche -> tranche.getRiskLevel().contains(riskScore))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("could not find tranche for risk score of: " + riskScore.getValue()));
    }

    private boolean doAllocateLoanToTranche(Loan loan, Tranche tranche, LocalDateTime timestamp) {

        /**
         * consumer:
         * - loan - exists/updated
         * - balance - not updated. TODO: should we update it here or when we add the loan. how balance is reflected?
         *
         * merchant:
         * - loan - exists/updated
         *
         * loan:
         * - consumer, merchant - updated
         * TODO: - once loan supports history of tranche this should be changed
         * - tranche - not updated - will be updated once we perform tranche.addLoan(loan) (bi-di association handles both sides)
         *
         * tranche:
         * - loan - not updated - performed here
         * - action history - performed here TODO - not complete impl
         * - balance history - TODO: should we update this as part of the action history (OR it will be calculated on demand by a method? -- NO!!!)
         */

        TrancheEvent event = TrancheEventLoanAdded.createTrancheEventLoanAdded(loan, tranche, timestamp, "tranche_service");

        return tranche.addLoan(loan, timestamp); // also calls loan.setTranche(tranche);
    }
}
