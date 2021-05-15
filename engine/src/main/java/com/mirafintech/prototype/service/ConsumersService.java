package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConsumersService {

    @Autowired
    private ConsumerRepository repository;

    @Autowired
    private TimeService timeService;


    public Consumer addConsumer(ConsumerDto consumer) {

        if (this.repository.findById(consumer.getId()).isPresent()) {
            throw new IllegalArgumentException("consumer already exists: id=" + consumer.getId());
        }

        // create consumer from dto incl dateTime
        //TODO
//        consumer.setAddedAt(timeService.getCurrentDateTime());

        // persist consumer
        //TODO:
//        return this.repository.saveAndFlush(consumer);
        return null;
    }
}
