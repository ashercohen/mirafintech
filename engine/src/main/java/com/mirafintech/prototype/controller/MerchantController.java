package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.MerchantDto;
import com.mirafintech.prototype.model.Merchant;
import com.mirafintech.prototype.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/merchants")
public class MerchantController implements ErrorHandler {

    @Autowired
    private MerchantService merchantService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Merchant> addMerchant(@RequestBody MerchantDto merchant, @PathVariable long id) {

        if (merchant.getId() != id) {
            return ResponseEntity.badRequest().body(null);
        }
        Merchant savedEntity = this.merchantService.addMerchant(merchant);

        return ResponseEntity.of(Optional.ofNullable(savedEntity));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Merchant> getMerchant(@PathVariable long id) {
        return ResponseEntity.of(this.merchantService.findById(id));
    }
}
