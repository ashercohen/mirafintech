package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.dto.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * handles configuration message sent at startup
 */
@Service
public class ConfigurationService {

    @Autowired
    private TranchesService tranchesService;

    public void apply(Configuration configuration) {
        tranchesService.initializeTranches(configuration.getRiskLevels());
    }
}
