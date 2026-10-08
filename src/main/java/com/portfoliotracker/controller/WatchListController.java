package com.portfoliotracker.controller;

import com.portfoliotracker.dto.AddWatchListRequest;
import com.portfoliotracker.exception.NotLoggedInException;
import com.portfoliotracker.model.WatchListItem;
import com.portfoliotracker.security.AuthInterceptor;
import com.portfoliotracker.service.WatchListService;
import jakarta.servlet.http.HttpSession;
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
    public List<WatchListItem> getWatchList(HttpSession session) {

        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        return watchListService.getWatchListByUserId(userId);
    }

    @PostMapping
    public WatchListItem addWatchListItem(@RequestBody AddWatchListRequest request, HttpSession session) {

        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        return watchListService.addWatchListItem(userId, request.getStockId());
    }

    @DeleteMapping("/{stockId}")
    public void removeWatchListItem(@PathVariable int stockId, HttpSession session) {

        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        watchListService.removeWatchListItem(userId, stockId);
    }
}