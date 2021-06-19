package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.ConsumerDto;
import com.mirafintech.prototype.model.consumer.Consumer;
import com.mirafintech.prototype.service.ConsumersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/consumers")
public class ConsumerController implements ErrorHandler {

    @Autowired
    private ConsumersService consumersService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Consumer> addConsumer(@RequestBody ConsumerDto consumer, @PathVariable long id) {

        if (consumer.id() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Consumer savedEntity = this.consumersService.addConsumer(consumer);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Consumer> getConsumer(@PathVariable long id) {
        return ResponseEntity.of(this.consumersService.findById(id));
    }
}
