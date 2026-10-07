package com.portfoliotracker.controller;


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
    public Holding addHolding(@RequestParam int userId, @RequestParam int stockId,
                              @RequestParam BigDecimal quantity, @RequestParam BigDecimal purchasePrice) {

        return holdingService.addHolding(userId, stockId, quantity, purchasePrice);
    }

    @PutMapping("/{holdingId}")
    public Holding updateHolding(@PathVariable int holdingId, @RequestParam BigDecimal quantity,
                                 @RequestParam BigDecimal purchasePrice) {

        return holdingService.updateHolding(holdingId, quantity, purchasePrice);
    }

    @DeleteMapping("/{holdingId}")
    public void removeHolding(@PathVariable int holdingId) {
        holdingService.removeHolding(holdingId);
    }


}
