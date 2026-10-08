package com.portfoliotracker.dto;

import java.math.BigDecimal;

public class UpdateHoldingRequest {

    private BigDecimal quantity;
    private BigDecimal averagePurchasePrice;

    public UpdateHoldingRequest() {
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAveragePurchasePrice() {
        return averagePurchasePrice;
    }

    public void setAveragePurchasePrice(BigDecimal averagePurchasePrice) {
        this.averagePurchasePrice = averagePurchasePrice;
    }
}