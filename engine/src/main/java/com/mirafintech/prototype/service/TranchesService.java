package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.RiskLevel;
import com.mirafintech.prototype.model.Tranche;
import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.repository.TrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class TranchesService {

    @Autowired
    private TrancheRepository repository;

    @Autowired
    private TimeService timeService;

    private List<Tranche> tranches = new ArrayList<>();

    public void initializeTranches(List<ConfigurationDto.RiskLevel> riskLevels) {

        LocalDateTime virtualTime = timeService.getCurrentDateTime();
        List<Tranche> tranches = riskLevels.stream()
                .map(riskLevelDTO ->
                        new RiskLevel(
                                riskLevelDTO.getLevel(),
                                riskLevelDTO.getLabel(),
                                riskLevelDTO.getLowerBound(),
                                riskLevelDTO.getUpperBound())
                )
                .map(riskLevel -> Tranche.createEmptyTranche(new BigDecimal("100000.0"), virtualTime, riskLevel))
                .toList();

        addTranches(tranches);
    }

    private void addTranches(Collection<Tranche> newTranches) {
        newTranches.forEach(this::addTranche);
    }

    private void addTranche(Tranche tranche) {
        Tranche persistedTranche = this.repository.save(tranche);
        this.tranches.add(persistedTranche);
    }

}
