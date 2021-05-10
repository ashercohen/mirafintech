package com.mirafintech.prototype.service;

import com.mirafintech.prototype.event.NewDateEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;


@Service
public class NewDateEventListener implements ApplicationListener<NewDateEvent> {

    @Override
    public void onApplicationEvent(NewDateEvent newDateEvent) {

        // TODO: implement this...
    }
}
