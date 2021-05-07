package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.model.Consumer;
import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.model.UCICreditCard;
import com.mirafintech.prototype.service.ConsumerService;
import com.mirafintech.prototype.service.TimeService;
import com.mirafintech.prototype.service.UCITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("messages")
public class MessageController {

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private UCITransactionService uciTransactionService;

    @RequestMapping(path = {"consumer/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Consumer> addConsumer(@RequestBody Consumer consumer, @PathVariable long id) {

        if (consumer.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Consumer savedEntity = this.consumerService.addConsumer(consumer);

        return ResponseEntity.of(Optional.of(savedEntity));
    }

    @RequestMapping(path = {"time/set"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LocalDateTime> setTime(@RequestBody String dateTime) {

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        SystemTime systemTime = this.timeService.setTime(localDateTime);

        return ResponseEntity.of(Optional.of(systemTime.getDateTime()));
    }

    @RequestMapping(path = {"uci/write/"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UCICreditCard> acceptTransaction(@RequestBody String csv) {

        UCICreditCard uciCreditCard = new UCICreditCard(csv);
        UCICreditCard savedEntity = this.uciTransactionService.writeUCICreditCard(uciCreditCard);

        return ResponseEntity.of(Optional.of(savedEntity));
    }

}
