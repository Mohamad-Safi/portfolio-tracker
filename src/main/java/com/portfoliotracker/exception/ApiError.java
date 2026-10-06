package com.portfoliotracker.exception;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class ApiError {

    private int status;
    private String message;
    private LocalDateTime timeStamp;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();//show the error when it was created.
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
