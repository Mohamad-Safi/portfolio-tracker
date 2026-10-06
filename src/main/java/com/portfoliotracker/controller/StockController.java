package com.portfoliotracker.controller;


import com.portfoliotracker.model.Stock;
import com.portfoliotracker.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }

    @GetMapping("/{stockId}")
    public Stock getStockById(@PathVariable int stockId){
        return stockService.getStockById(stockId);
    }
}
