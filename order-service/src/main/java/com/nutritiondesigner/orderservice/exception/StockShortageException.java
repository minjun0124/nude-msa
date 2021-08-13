package com.nutritiondesigner.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockShortageException extends RuntimeException {
    public StockShortageException(String message) {
        super(message);
    }
}
