package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.model.*;
import com.mirafintech.prototype.repository.TrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Tranche allocateLoanToTranche(Loan loan) {

        LocalDateTime timestamp = this.timeService.getCurrentDateTime();
        Tranche tranche = findTranche(loan.currentRiskScore(), loan.getAmount());
        boolean allocated = doAllocateLoanToTranche(loan, tranche, timestamp);
        // TODO: check this
        persistTranche(tranche);

        return tranche;
    }

    public int initializeTranches(List<ConfigurationDto.TrancheConfig> trancheConfigs) {

        LocalDateTime timestamp = timeService.getCurrentDateTime();
        trancheConfigs.sort(Comparator.comparing(ConfigurationDto.TrancheConfig::getLowerBoundRiskScore));

        long addedCount =
                IntStream.range(0, trancheConfigs.size())
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
                        .filter(this::persistTranche)
                        .count();

        if (addedCount != trancheConfigs.size()) {
            throw new RuntimeException(String.format("failed to initialize tranches: requested=%d, actual=%d", trancheConfigs.size(), addedCount));
        }

        return trancheConfigs.size();
    }

    private boolean persistTranche(Tranche tranche) {
        Tranche persistedTranche = this.repository.saveAndFlush(tranche);
        return this.tranches.add(persistedTranche);
    }

    /**
     * find a tranche with suitable risk level and balance
     * create new tranche if none found
     */
    private Tranche findTranche(TimedRiskScore loanRiskScore, BigDecimal amount) {

        List<Tranche> tranches =
                this.tranches.stream()
                        .filter(tranche -> tranche.getRiskLevel().contains(loanRiskScore.getRiskScore()))
                        .filter(tranche -> tranche.currentBalance().compareTo(amount) <= 0)
                        .toList();

        return switch (tranches.size()) {
            case 0 -> {
                // no matching tranche - allocate new tranche
                Tranche tranche = findBy(loanRiskScore.getRiskScore());
                yield allocateNewTrancheLike(tranche);
            }
            case 1 -> {
                // one matching tranche - return it
                yield tranches.get(0);
            }
            default -> {
                // multiple matches - return any // TODO: selecting one of many matching tranches requires additional logic
                yield tranches.get(0);
            }
        };
    }

    /**
     * create a new empty tranche similar to the provided one in terms of initial value and risk level
     */
    private Tranche allocateNewTrancheLike(Tranche tranche) {

        return Tranche.createEmptyTranche(
                tranche.getInitialValue(),
                this.timeService.getCurrentDateTime(),
                tranche.getRiskLevel().getId(),
                tranche.getRiskLevel().getLowerBound(),
                tranche.getRiskLevel().getUpperBound());
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
         * - tranche - not updated - will be updated once we perform tranche.addLoan(loan) (bi-di association handles both sides) TODO: - once loan supports history of tranche this should be changed
         *
         * tranche:
         * - loan - not updated - performed here
         * - action history - performed here TODO - not complete impl
         * - balance history - TODO: should we update this as part of the action history OR it will be calculated on demand by a method?
         */

        TrancheEvent event = new TrancheEvent(timestamp, tranche, TrancheEvent.Type.LOAN_ADDED);
        tranche.addTrancheEvent(event);

        // TODO: balance / event handling

        return tranche.addLoan(loan, timestamp); // also calls loan.setTranche(tranche);
    }
}
