package com.mirafintech.prototype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


public interface ErrorHandler {

    /**
     * custom exception handler for endpoints that define request body validation
     */
    @ExceptionHandler(value = {Exception.class})
    default ResponseEntity<?> handleException(Exception e) {

        e.printStackTrace();

        if (e instanceof IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        }

        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
