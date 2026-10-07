package com.portfoliotracker.exception;

public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException() {
        super("Username already taken");
    }
}
