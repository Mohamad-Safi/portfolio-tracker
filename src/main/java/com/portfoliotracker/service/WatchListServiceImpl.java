package com.portfoliotracker.service;

import com.portfoliotracker.dao.WatchListDao;
import com.portfoliotracker.model.WatchListItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchListServiceImpl implements WatchListService {

    private final WatchListDao watchListDao;

    public WatchListServiceImpl(WatchListDao watchListDao) {
        this.watchListDao = watchListDao;
    }

    @Override
    public WatchListItem addWatchListItem(int userId, int stockId) {

        WatchListItem existingItem = watchListDao.findWatchListItem(userId, stockId);

        if (existingItem != null) {
            throw new IllegalArgumentException("Stock is already in the watchlist");
        }

        WatchListItem item = new WatchListItem();
        item.setUserId(userId);
        item.setStockId(stockId);

        return watchListDao.addWatchListItem(item);
    }

    @Override
    public List<WatchListItem> getWatchListByUserId(int userId) {
        return watchListDao.getWatchListByUserId(userId);
    }

    @Override
    public void removeWatchListItem(int userId, int stockId) {

        WatchListItem existingItem =
                watchListDao.findWatchListItem(userId, stockId);

        if (existingItem == null) {
            throw new IllegalArgumentException(
                    "Stock is not in the watchlist");
        }

        watchListDao.removeWatchListItem(userId, stockId);
    }


}