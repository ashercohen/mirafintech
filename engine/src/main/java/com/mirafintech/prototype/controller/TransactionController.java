package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.model.UCICreditCard;
import com.mirafintech.prototype.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;


    @RequestMapping(path = {"write/"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UCICreditCard> acceptTransaction(@RequestBody String csv) {

        UCICreditCard uciCreditCard = new UCICreditCard(csv);

        UCICreditCard savedEntity = this.transactionService.writeUCICreditCard(uciCreditCard);

        return ResponseEntity.of(Optional.of(savedEntity));
    }

}
