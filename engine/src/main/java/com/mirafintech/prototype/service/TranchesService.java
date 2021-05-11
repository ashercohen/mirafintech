package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.Tranche;
import com.mirafintech.prototype.repository.TrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class TranchesService {

    @Autowired
    private TrancheRepository repository;


    public void initializeTranches() {

        Map<Tranche.RiskLevel, Tranche> trancheMap = Arrays.stream(Tranche.RiskLevel.values())
                .collect(Collectors.toMap(Function.identity(), risk -> new Tranche(new BigDecimal("100_000"), risk)));

        List<Tranche> persistedTranches = this.repository.saveAll(trancheMap.values());
    }

}
