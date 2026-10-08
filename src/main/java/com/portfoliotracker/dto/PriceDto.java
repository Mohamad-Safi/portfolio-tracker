package com.portfoliotracker.dto;

import java.math.BigDecimal;


//record to store price
//{"tickerSymbol": "AAPL", "price": 336.67} in json shape
public record PriceDto(
        String tickerSymbol,
        BigDecimal price) {
}
