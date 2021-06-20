package com.mirafintech.prototype.controller;

import com.mirafintech.prototype.model.tranche.Tranche;
import com.mirafintech.prototype.service.TranchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/tranches")
public class TrancheController implements ErrorHandler {

    @Autowired
    private TranchesService tranchesService;

    @Transactional(readOnly = true)
    @RequestMapping(path = {"/{id}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Tranche> getTranche(@PathVariable long id) {
        return ResponseEntity.of(this.tranchesService.findById(id));
    }

    @Transactional(readOnly = true)
    @RequestMapping(path = {""}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Tranche>> getTranches() {
        return ResponseEntity.ok(this.tranchesService.findAll());
    }
}
