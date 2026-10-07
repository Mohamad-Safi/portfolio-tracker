package com.portfoliotracker.service;

import com.portfoliotracker.dao.StockDao;
import com.portfoliotracker.dto.HoldingDao;      // same import as in PortfolioServiceImpl
import com.portfoliotracker.dto.PortfolioSummaryDto;
import com.portfoliotracker.dto.PositionDto;
import com.portfoliotracker.model.Holding;
import com.portfoliotracker.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock private HoldingDao holdingDao;
    @Mock private StockDao stockDao;
    @Mock private PriceService priceService;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;
    //build test data
    private Holding holding(int stockId, String quantity, String averagePrice){
        Holding h = new Holding();
        h.setUserId(1);
        h.setStockId(stockId);
        h.setQuantity(new BigDecimal(quantity));
        h.setAveragePurchasePrice(new BigDecimal(averagePrice));
        return h;
    }

    private Stock stock(int stockId, String ticker, String name){
        Stock s = new Stock();
        s.setStockId(stockId);
        s.setTickerSymbol(ticker);
        s.setCompanyName(name);
        return s;
    }

    @Test
    void getPortfolioSummary_oneHoldingWithPrice_calculatesAllNumbers(){
        when(holdingDao.getHoldingsByUserId(1)).thenReturn(List.of(holding(1, "11"
        ,"153.3333")));
        when(stockDao.getStockById(1)).
                thenReturn(Optional.of(stock(1, "AAPL", "Apple Inc")));

        when(priceService.getCurrentPrice("AAPL"))
                .thenReturn(Optional.of(new BigDecimal("190.00")));

        PortfolioSummaryDto summary = portfolioService.getPortfolioSummary(1);
        assertEquals(1, summary.positions().size());
        PositionDto aapl = summary.positions().get(0);

        assertEquals("AAPL", aapl.tickerSymbol());
        assertEquals(new BigDecimal("1686.67"), aapl.cost());
        assertEquals(new BigDecimal("190.00"),  aapl.currentPrice());
        assertEquals(new BigDecimal("2090.00"), aapl.marketValue());
        assertEquals(new BigDecimal("403.33"),  aapl.gain());
        assertEquals(new BigDecimal("23.91"),   aapl.gainPercent());

        assertEquals(new BigDecimal("1686.67"), summary.totalCost());
        assertEquals(new BigDecimal("2090.00"), summary.totalValue());
        assertEquals(new BigDecimal("403.33"),  summary.totalGain());
        assertEquals(new BigDecimal("23.91"),   summary.totalGainPercent());
        assertTrue(summary.allPricesAvailable());
    }

    @Test
    void getPortfolioSummary_oneHoldingWithoutPrice_excludedFromTotals() {
        when(holdingDao.getHoldingsByUserId(1))
                .thenReturn(List.of(
                        holding(1, "11", "153.3333"),
                        holding(2, "4", "250.00")));

        when(stockDao.getStockById(1)).thenReturn(Optional.of(stock(1, "AAPL", "Apple Inc")));
        when(stockDao.getStockById(2)).thenReturn(Optional.of(stock(2, "TSLA", "Tesla Inc")));

        when(priceService.getCurrentPrice("AAPL")).thenReturn(Optional.of(new BigDecimal("190.00")));
        when(priceService.getCurrentPrice("TSLA")).thenReturn(Optional.empty());

        PortfolioSummaryDto summary = portfolioService.getPortfolioSummary(1);

        assertEquals(2, summary.positions().size());

        PositionDto tsla = summary.positions().get(1);
        assertEquals("TSLA", tsla.tickerSymbol());
        // cost only needs quantity × average price, so it's still known!! 4 × 250.00
        assertEquals(new BigDecimal("1000.00"), tsla.cost());
        // everything that needs a live price is null ("price unavailable" on the page).
        assertNull(tsla.currentPrice());
        assertNull(tsla.marketValue());
        assertNull(tsla.gain());
        assertNull(tsla.gainPercent());

        //totals contain ONLY AAPL. TSLA's 1000.00 cost is NOT
        // included, otherwise the total would show a fake loss.
        assertEquals(new BigDecimal("1686.67"), summary.totalCost());
        assertEquals(new BigDecimal("2090.00"), summary.totalValue());
        assertEquals(new BigDecimal("403.33"),  summary.totalGain());
        assertEquals(new BigDecimal("23.91"),   summary.totalGainPercent());
        assertFalse(summary.allPricesAvailable());
    }
    @Test
    void getPortfolioSummary_noHoldings_returnsEmptySummary() {

        //ser 1 owns nothing
        when(holdingDao.getHoldingsByUserId(1)).thenReturn(List.of());
        PortfolioSummaryDto summary = portfolioService.getPortfolioSummary(1);
        assertTrue(summary.positions().isEmpty());

        // ASSERT 2: every total is zero. round2 turns BigDecimal.ZERO into 0.00
        // (2 decimal places), so we compare with "0.00".
        // totalGainPercent stays 0.00 because the zero check SKIPPED the
        // division: this is the line that would otherwise crash.
        assertEquals(new BigDecimal("0.00"), summary.totalCost());
        assertEquals(new BigDecimal("0.00"), summary.totalValue());
        assertEquals(new BigDecimal("0.00"), summary.totalGain());
        assertEquals(new BigDecimal("0.00"), summary.totalGainPercent());

        //nothing was missing, so make suire flag is true !
        assertTrue(summary.allPricesAvailable());

        // with no holdings, there's nothing to look up or price.
        // verify no interactions proves this(need to learn no interactions!!)
        verifyNoInteractions(stockDao);
        verifyNoInteractions(priceService);
    }

}
