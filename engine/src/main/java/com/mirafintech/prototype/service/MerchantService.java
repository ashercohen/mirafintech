package com.mirafintech.prototype.service;

import com.mirafintech.prototype.dto.MerchantDto;
import com.mirafintech.prototype.model.Merchant;
import com.mirafintech.prototype.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MerchantService {

    @Autowired
    private MerchantRepository repository;

    @Autowired
    private TimeService timeService;

    public Optional<Merchant> findMerchant(long merchantId) {

        return this.repository
                .findById(merchantId);
    }

    public Merchant addMerchant(MerchantDto merchantDto) {

        this.repository
                .findById(merchantDto.getId())
                .ifPresent(merchant -> {
                    throw new IllegalArgumentException("merchant already exists: id=" + merchantDto.getId());
                });

        Merchant merchant = new Merchant(merchantDto.getId(), merchantDto.getName(),  this.timeService.getCurrentDateTime());

        return this.repository.saveAndFlush(merchant);
    }

    public Optional<Merchant> findById(long id) {
        return this.repository.findById(id);
    }
}
