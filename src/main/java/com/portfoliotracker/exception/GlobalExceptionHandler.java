package com.portfoliotracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ApiError> handleStockNotFound(StockNotFoundException ex){
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidSymbolException.class)
    public ResponseEntity<ApiError> handleInvalidSymbol(InvalidSymbolException ex){
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PriceServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleUnavailablePrice(PriceServiceUnavailableException ex){
        ApiError error = new ApiError(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

}
