package com.portfoliotracker.service;


import com.portfoliotracker.dao.StockDao;
import com.portfoliotracker.exception.InvalidSymbolException;
import com.portfoliotracker.exception.StockNotFoundException;
import com.portfoliotracker.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTests {

    @Mock
    private StockDao stockDao;
    @Mock
    private PriceService priceService;
    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void getStockById_existingId_returnsTheStock(){
        Stock apple = new Stock();
        apple.setStockId(1);
        apple.setTickerSymbol("AAPL");
        apple.setCompanyName("Apple Inc");

        when(stockDao.getStockById(1)).thenReturn(Optional.of(apple));

        Stock result = stockService.getStockById(1);
        assertEquals("AAPL", result.getTickerSymbol());
        assertEquals("Apple Inc", result.getCompanyName());
    }

    @Test
    void getStockById_missingId_throwsStockNotFoundException(){

        when(stockDao.getStockById(999)).thenReturn(Optional.empty());
        assertThrows(StockNotFoundException.class, () -> stockService.getStockById(999));
    }

    @Test
    void findOrCreateStock_existingTicker_returnsItWithoutApiCallOrSave() {
        Stock apple = new Stock();
        apple.setStockId(1);
        apple.setTickerSymbol("AAPL");
        apple.setCompanyName("Apple Inc");

        when(stockDao.getStockByTicker("AAPL")).thenReturn(Optional.of(apple));

        Stock result = stockService.findOrCreateStock(" aapl ");

        assertEquals(1, result.getStockId());
        assertEquals("AAPL", result.getTickerSymbol());

        verify(priceService, never()).lookUpCompanyName(anyString());
        verify(stockDao, never()).addStock(any(Stock.class));
    }

    @Test
    void findOrCreateStock_newRealTicker_looksUpNameAndSaves() {
        Stock saved = new Stock();
        saved.setStockId(7);
        saved.setTickerSymbol("TSLA");
        saved.setCompanyName("Tesla Inc");
    }

    @Test
    void findOrCreateStock_fakeTicker_throwsAndSavesNothing() {


        when(stockDao.getStockByTicker("ZZZZ")).thenReturn(Optional.empty());
        when(priceService.lookUpCompanyName("ZZZZ"))
                .thenThrow(new InvalidSymbolException("We could not find the stock: ZZZZ"));

        assertThrows(InvalidSymbolException.class,
                () -> stockService.findOrCreateStock("ZZZZ"));
        verify(stockDao, never()).addStock(any(Stock.class));
    }






}
