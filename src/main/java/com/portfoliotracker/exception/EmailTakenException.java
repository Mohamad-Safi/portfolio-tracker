package com.portfoliotracker.exception;

public class EmailTakenException extends RuntimeException {

    public EmailTakenException() {
        super("Email already taken");
    }
}
