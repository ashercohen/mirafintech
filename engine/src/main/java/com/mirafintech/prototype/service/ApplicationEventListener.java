package com.mirafintech.prototype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * event listener for application events
 */
@Service
public class ApplicationEventListener {

    @Autowired
    private TranchesService tranchesService;

    /**
     * ContextRefreshedEvent is fired once spring context has been initialized or refreshed
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("ContextRefreshedEvent=" + event);
        // initialize tranches
        this.tranchesService.initializeTranches();
    }
}
