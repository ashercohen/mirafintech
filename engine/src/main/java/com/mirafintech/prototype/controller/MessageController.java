package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.model.UCICreditCard;
import com.mirafintech.prototype.service.UCITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/")
public class MessageController implements ErrorHandler {

    @Autowired
    private UCITransactionService uciTransactionService;

    /**
     * this endpoint is for testing only - loads a row of the UCI dataset into the database
     */
    @RequestMapping(path = {"uci/write/"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UCICreditCard> acceptTransaction(@RequestBody String csv) {

        UCICreditCard uciCreditCard = new UCICreditCard(csv);
        UCICreditCard savedEntity = this.uciTransactionService.writeUCICreditCard(uciCreditCard);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }
}
