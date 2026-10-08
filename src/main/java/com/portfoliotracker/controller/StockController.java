package com.portfoliotracker.controller;


import com.portfoliotracker.dto.AddStockRequestDto;
import com.portfoliotracker.dto.PriceDto;
import com.portfoliotracker.exception.PriceServiceUnavailableException;
import com.portfoliotracker.model.Stock;
import com.portfoliotracker.service.PriceService;
import com.portfoliotracker.service.StockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;
    private final PriceService priceService;

    public StockController(StockService stockService, PriceService priceService) {
        this.stockService = stockService;
        this.priceService = priceService;
    }

    //live price else 404 or 503 if not price is available
    @GetMapping("/{stockId}/price")
    public PriceDto getStockPrice(@PathVariable int stockId){
        Stock stock = stockService.getStockById(stockId);
        Optional<BigDecimal> price = priceService.getCurrentPrice(stock.getTickerSymbol());
        if (price.isEmpty()){
            throw new PriceServiceUnavailableException("Price is currently not available for "
            + stock.getTickerSymbol());
        }
        BigDecimal roundedPrice = price.get().setScale(2, RoundingMode.HALF_UP);
        return new PriceDto(stock.getTickerSymbol(), roundedPrice);

    }

    @GetMapping
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }

    @GetMapping("/{stockId}")
    public Stock getStockById(@PathVariable int stockId){
        return stockService.getStockById(stockId);
    }

    @PostMapping
    public Stock addStock(@Valid @RequestBody AddStockRequestDto request) {
        return stockService.findOrCreateStock(request.tickerSymbol());
    }
}
