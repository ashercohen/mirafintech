package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.dto.ConfigurationDto;
import com.mirafintech.prototype.model.SystemTime;
import com.mirafintech.prototype.service.ConfigurationService;
import com.mirafintech.prototype.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/set")
public class ConfigurationController implements ErrorHandler {

    @Autowired
    private TimeService timeService;

    @Autowired
    private ConfigurationService configurationService;

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/config"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> setConfiguration(@RequestBody ConfigurationDto configuration) {

        this.timeService.setTime(configuration.getInitTimestamp());
        int numTranches = this.configurationService.apply(configuration);

        return ResponseEntity.ok().body(String.format("initialized %d tranches", numTranches));
    }

    @Transactional(readOnly = false)
    @RequestMapping(path = {"/time"}, method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LocalDateTime> setTime(@RequestBody String dateTime) {

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        SystemTime systemTime = this.timeService.setTime(localDateTime);

        return ResponseEntity.of(Optional.ofNullable(systemTime.getDateTime()));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/time"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LocalDateTime> getTime() {
        return ResponseEntity.ok(this.timeService.getCurrentDateTime());
    }
}
