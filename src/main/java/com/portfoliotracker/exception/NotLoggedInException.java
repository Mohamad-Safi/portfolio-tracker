package com.portfoliotracker.exception;

public class NotLoggedInException extends RuntimeException {

    public NotLoggedInException() {
        super("Not logged in");
    }
}
