package com.portfoliotracker.controller;

import com.portfoliotracker.dto.AddWatchListRequest;
import com.portfoliotracker.model.WatchListItem;
import com.portfoliotracker.service.WatchListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchListController {

    private final WatchListService watchListService;

    public WatchListController(WatchListService watchListService) {
        this.watchListService = watchListService;
    }

    @GetMapping
    public List<WatchListItem> getWatchList(@RequestParam int userId) {
        return watchListService.getWatchListByUserId(userId);
    }

    @PostMapping
    public WatchListItem addWatchListItem(@RequestBody AddWatchListRequest request) {

        return watchListService.addWatchListItem(
                request.getUserId(),
                request.getStockId()
        );
    }

    @DeleteMapping("/{stockId}")
    public void removeWatchListItem(
            @PathVariable int stockId,
            @RequestParam int userId) {

        watchListService.removeWatchListItem(userId, stockId);
    }
}
