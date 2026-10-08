package com.portfoliotracker.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSummaryDto(
        List<PositionDto> positions,
        BigDecimal totalCost,
        BigDecimal totalValue,
        BigDecimal totalGain,
        BigDecimal totalGainPercent,
        boolean allPricesAvailable
) {
}
