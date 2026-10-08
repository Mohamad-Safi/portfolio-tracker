package com.portfoliotracker.controller;


import com.portfoliotracker.dto.AddHoldingRequest;
import com.portfoliotracker.dto.UpdateHoldingRequest;
import com.portfoliotracker.exception.NotLoggedInException;
import com.portfoliotracker.model.Holding;
import com.portfoliotracker.security.AuthInterceptor;
import com.portfoliotracker.service.HoldingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/holdings")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @GetMapping
    public List<Holding> getHoldings(HttpSession session) {

        Integer userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        if (userId == null) {
            throw new NotLoggedInException();
        }

        return holdingService.getHoldingsByUserId(userId);
    }

    @GetMapping("/{holdingId}")
    public Holding getHolding(@PathVariable int holdingId, HttpSession session) {
        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);
        return holdingService.getHoldingById(userId, holdingId);
    }

    @PostMapping
    public Holding addHolding(@RequestBody AddHoldingRequest request, HttpSession session) {

        Integer userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);
        if (userId == null) {
            throw new NotLoggedInException();
        }

        return holdingService.addHolding(
                userId,
                request.getStockId(),
                request.getQuantity(),
                request.getPurchasePrice());
    }

    @PutMapping("/{holdingId}")
    public Holding updateHolding(@PathVariable int holdingId, @RequestBody UpdateHoldingRequest request,
                                 HttpSession session) {

        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        return holdingService.updateHolding(
                userId,
                holdingId,
                request.getQuantity(),
                request.getAveragePurchasePrice());
    }

    @DeleteMapping("/{holdingId}")
    public void removeHolding(@PathVariable int holdingId, HttpSession session) {

        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        holdingService.removeHolding(userId, holdingId);
    }


}