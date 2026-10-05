package com.portfoliotracker.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Position {
    private Stock stock;
    private BigDecimal sharesOwned = BigDecimal.ZERO;
    // total cost of the shares still owned
    private BigDecimal costBasis = BigDecimal.ZERO;
    // profit or loss locked in by selling
    private BigDecimal realisedGain = BigDecimal.ZERO;

    public BigDecimal getAverageCost() {
        if (sharesOwned.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return costBasis.divide(sharesOwned, 4, RoundingMode.HALF_UP);
    }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public BigDecimal getSharesOwned() { return sharesOwned; }
    public void setSharesOwned(BigDecimal sharesOwned) { this.sharesOwned = sharesOwned; }

    public BigDecimal getCostBasis() { return costBasis; }
    public void setCostBasis(BigDecimal costBasis) { this.costBasis = costBasis; }

    public BigDecimal getRealisedGain() { return realisedGain; }
    public void setRealisedGain(BigDecimal realisedGain) { this.realisedGain = realisedGain; }
}