package com.portfoliotracker.service;


import com.portfoliotracker.dao.StockDao;
import com.portfoliotracker.exception.StockNotFoundException;
import com.portfoliotracker.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTests {

    @Mock
    private StockDao stockDao;

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



}
