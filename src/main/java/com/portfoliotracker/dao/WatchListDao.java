package com.portfoliotracker.dao;

import com.portfoliotracker.model.WatchListItem;
import java.util.List;

public interface WatchListDao {

    WatchListItem addWatchListItem(WatchListItem item);

    List<WatchListItem> getWatchListByUserId(int userId);

    WatchListItem findWatchListItem(int userId, int stockId);

    void removeWatchListItem(int userId, int stockId);
}