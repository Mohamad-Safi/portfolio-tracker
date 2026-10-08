package com.portfoliotracker.dto;

public class AddWatchListRequest {

    private int stockId;

    public AddWatchListRequest() {
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
}