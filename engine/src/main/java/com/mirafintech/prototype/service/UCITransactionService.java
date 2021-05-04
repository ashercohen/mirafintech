package com.mirafintech.prototype.service;

import com.mirafintech.prototype.model.UCICreditCard;
import com.mirafintech.prototype.repository.UCICreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UCITransactionService {

    @Autowired
    UCICreditCardRepository uciCreditCardRepository;

    public UCICreditCard writeUCICreditCard(UCICreditCard uciCreditCard) {

        return this.uciCreditCardRepository.save(uciCreditCard);
    }
}
