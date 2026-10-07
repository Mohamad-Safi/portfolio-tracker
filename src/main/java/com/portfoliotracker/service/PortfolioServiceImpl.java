package com.portfoliotracker.service;


import com.portfoliotracker.dao.StockDao;
import com.portfoliotracker.dto.HoldingDao;//this needs to go into the dao package

import com.portfoliotracker.dto.PortfolioSummaryDto;
import com.portfoliotracker.dto.PositionDto;
import com.portfoliotracker.exception.StockNotFoundException;
import com.portfoliotracker.model.Holding;
import com.portfoliotracker.model.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final HoldingDao holdingDao;
    private final StockDao stockDao;

    private final PriceService priceService;

    public PortfolioServiceImpl(HoldingDao holdingDao, StockDao stockDao, PriceService priceService) {
        this.holdingDao = holdingDao;
        this.stockDao = stockDao;
        this.priceService = priceService;
    }

    @Override
    public PortfolioSummaryDto getPortfolioSummary(int userId){
        List<Holding> holdings = holdingDao.getHoldingsByUserId(userId);
        List<PositionDto> positions = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        boolean allPricesAvailable = true;

        for (Holding holding : holdings){
            PositionDto position = buildPosition(holding);
            positions.add(position);
            if (position.marketValue()==null){
                allPricesAvailable = false;
            }else{
                totalCost = totalCost.add(position.cost());
                totalValue = totalValue.add(position.marketValue());
            }
        }
        BigDecimal totalGain = totalValue.subtract(totalCost);
        BigDecimal totalGainPercent = BigDecimal.ZERO;
        if (totalCost.compareTo(BigDecimal.ZERO)>0){
            totalGainPercent = totalGain.divide(totalCost, 6, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return new PortfolioSummaryDto(
                positions,
                round2(totalCost),
                round2(totalValue),
                round2(totalGain),
                round2(totalGainPercent),
                allPricesAvailable
        );

    }
    //helper to round to 2dp (keep null values as null)
    private BigDecimal round2(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }

    private PositionDto buildPosition(Holding holding){
        Optional<Stock> stockBox = stockDao.getStockById(holding.getStockId());
        if (stockBox.isEmpty()){//if there is no stock throw exception otherwise return that stock
            throw new StockNotFoundException("No stock found with id "+ holding.getStockId());
        }
        Stock stock = stockBox.get();
        BigDecimal quantity = holding.getQuantity();
        BigDecimal averagePrice = holding.getAveragePurchasePrice();
        BigDecimal cost = quantity.multiply(averagePrice);
        Optional<BigDecimal> priceBox = priceService.getCurrentPrice(stock.getTickerSymbol());
        //keep null unless a price is returned from priceBox
        BigDecimal currentPrice = null;
        BigDecimal marketValue = null;
        BigDecimal gain = null;
        BigDecimal gainPercent = null;

        if (priceBox.isPresent()){
            currentPrice = priceBox.get();
            marketValue = quantity.multiply(currentPrice);
            gain = marketValue.subtract(cost);
            gainPercent = gain.divide(cost, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }

        return new PositionDto(
                stock.getTickerSymbol(),
                stock.getCompanyName(),
                quantity,
                averagePrice,
                round2(currentPrice),
                round2(cost),
                round2(marketValue),
                round2(gain),
                round2(gainPercent)
        );
    }
}
