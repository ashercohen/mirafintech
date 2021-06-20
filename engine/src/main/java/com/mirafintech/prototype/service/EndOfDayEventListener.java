package com.mirafintech.prototype.service;

import com.mirafintech.prototype.event.EndOfDayEvent;
import com.mirafintech.prototype.model.charge.InterestCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
public class EndOfDayEventListener implements ApplicationListener<EndOfDayEvent> {

    @Autowired
    private LoanService loanService;

    @Override
    public void onApplicationEvent(EndOfDayEvent event) {

        System.out.printf("handling event nanosOfDay=%d event=%s\n", LocalTime.now().toNanoOfDay(), event);

        /**
         * TODO:
         *  - check due payment
         *  - create interest charges
         *  - create late payment notifications
         *  - check if consumer risk level changed - this might trigger moving loans between tranches
         *  - more...
         */
        List<InterestCharge> interestCharges = generateInterestCharges(event.getDayEnded());
        System.out.println();
    }

    private List<InterestCharge> generateInterestCharges(LocalDate date) {
        return loanService.generateInterestCharges(date);
    }
}
