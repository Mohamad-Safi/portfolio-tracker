package com.portfoliotracker.controller;


import com.portfoliotracker.dto.AddHoldingRequest;
import com.portfoliotracker.dto.UpdateHoldingRequest;
import com.portfoliotracker.model.Holding;
import com.portfoliotracker.service.HoldingService;
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
    public List<Holding> getHoldings(@RequestParam int userId) {
        return holdingService.getHoldingsByUserId(userId);
    }

    @GetMapping("/{holdingId}")
    public Holding getHolding(@PathVariable int holdingId) {
        return holdingService.getHoldingById(holdingId);
    }

    @PostMapping
    public Holding addHolding(@RequestBody AddHoldingRequest request) {

        return holdingService.addHolding(
                request.getUserId(),
                request.getStockId(),
                request.getQuantity(),
                request.getPurchasePrice()
        );
    }

    @PutMapping("/{holdingId}")
    public Holding updateHolding(
            @PathVariable int holdingId,
            @RequestBody UpdateHoldingRequest request) {

        return holdingService.updateHolding(
                holdingId,
                request.getQuantity(),
                request.getAveragePurchasePrice()
        );
    }

    @DeleteMapping("/{holdingId}")
    public void removeHolding(@PathVariable int holdingId) {
        holdingService.removeHolding(holdingId);
    }


}