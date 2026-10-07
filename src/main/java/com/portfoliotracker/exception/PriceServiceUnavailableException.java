package com.portfoliotracker.exception;

public class PriceServiceUnavailableException extends RuntimeException {
    public PriceServiceUnavailableException(String message) {
        super(message);
    }
}
