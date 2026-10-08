package com.portfoliotracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// What the front end sends: {"tickerSymbol": "TSLA"}
public record AddStockRequestDto(
        @NotBlank(message = "Ticker is required")          // (1)
        @Size(max = 10, message = "Ticker is too long")    // (2)
        String tickerSymbol
) {}