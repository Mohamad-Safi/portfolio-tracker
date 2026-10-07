package com.portfoliotracker.controller;

import com.portfoliotracker.service.PriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TEMPORARY: only to see PriceService working. Delete before merging.
@RestController
@RequestMapping("/api/v1/price-check")
public class PriceCheckController {

    private final PriceService priceService;

    public PriceCheckController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/{tickerSymbol}")
    public String check(@PathVariable String tickerSymbol) {
            return "price: " + priceService.getCurrentPrice(tickerSymbol);

    }
}