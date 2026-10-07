package com.portfoliotracker.dto;

import java.math.BigDecimal;

public record PositionDto (
        String tickerSymbol,
         String companyName,
         BigDecimal quantity,
         BigDecimal averagePurchasePrice,
         BigDecimal currentPrice,
         BigDecimal cost,
         BigDecimal marketValue,
         BigDecimal gain,
         BigDecimal gainPercent){


}
