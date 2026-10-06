package com.portfoliotracker.model;

import java.time.LocalDateTime;

public class WatchListItem {

    private int watchListItemId;
    private int userId;
    private int stockId;
    private LocalDateTime addedAt;

    public int getWatchListItemId() {
        return watchListItemId;
    }

    public void setWatchListItemId(int watchListItemId) {
        this.watchListItemId = watchListItemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStock() {
        return stockId;
    }

    public void setStock(int stockId) {
        this.stockId = stockId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
