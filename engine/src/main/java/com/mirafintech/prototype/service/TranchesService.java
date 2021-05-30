package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.model.Loan;
import com.mirafintech.prototype.model.Tranche;
import com.mirafintech.prototype.model.RiskScore;
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
        return null;
//        throw new RuntimeException("allocateLoanToTranche not implemented yet");
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
}
