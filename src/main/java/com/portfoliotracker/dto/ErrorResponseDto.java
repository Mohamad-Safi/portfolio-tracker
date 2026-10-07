package com.portfoliotracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        String error,
        Map<String, String> fields
) {

    public static ErrorResponseDto of(String error) {
        return new ErrorResponseDto(error, null);
    }
}
