package com.portfoliotracker.service;

import com.portfoliotracker.model.WatchListItem;

import java.util.List;

public interface WatchListService {

    WatchListItem addWatchListItem(int userId, int stockId);

    List<WatchListItem> getWatchListByUserId(int userId);

    void removeWatchListItem(int userId, int stockId);
}
