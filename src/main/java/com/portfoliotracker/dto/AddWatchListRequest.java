package com.portfoliotracker.dto;

public class AddWatchListRequest {

    private int userId;
    private int stockId;

    public AddWatchListRequest() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStockId() {
        return stockId;
    }

}