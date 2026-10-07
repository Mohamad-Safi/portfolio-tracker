package com.portfoliotracker.dao;


import com.portfoliotracker.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;

import static org.junit.jupiter.api.Assertions.*;


import java.util.List;
import java.util.Optional;



@SpringBootTest
@ActiveProfiles({"local", "test"})
@Transactional
class StockDaoImplTest {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        //start with empty table
        jdbcTemplate.update("DELETE FROM transaction_history");
        jdbcTemplate.update("DELETE FROM watchlist_item");
        jdbcTemplate.update("DELETE FROM holding");
        jdbcTemplate.update("DELETE FROM stock");

        // MSFT goes in first on purpose, to prove ORDER BY really sorts
        jdbcTemplate.update("INSERT INTO stock (stockId, tickerSymbol, companyName) VALUES "
                + "(1, 'MSFT', 'Microsoft Corporation'), (2, 'AAPL', 'Apple Inc')");
    }

    @Test
    void getAllStocks_returnsEveryStockSortedByTicker() {
        List<Stock> stocks = stockDao.getAllStocks();

        assertEquals(2, stocks.size());
        assertEquals("AAPL", stocks.get(0).getTickerSymbol());
        assertEquals("MSFT", stocks.get(1).getTickerSymbol());
    }

    @Test
    void getStockById_existingId_returnsThatStock() {
        Optional<Stock> result = stockDao.getStockById(1);

        assertTrue(result.isPresent());
        assertEquals("MSFT", result.get().getTickerSymbol());
        assertEquals("Microsoft Corporation", result.get().getCompanyName());
    }

    @Test
    void getStockById_missingId_returnsEmpty() {
        Optional<Stock> result = stockDao.getStockById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void getStockByTicker_exactTicker_returnsThatStock(){
        Optional<Stock> result = stockDao.getStockByTicker("AAPL");
        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getTickerSymbol());
        assertEquals("Apple Inc", result.get().getCompanyName());

    }

    @Test
    void getStockByTicker_lowerCaseTicker_shouldStillFindIt(){
        Optional<Stock> result = stockDao.getStockByTicker("aapl");
        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getTickerSymbol());
        assertEquals("Apple Inc", result.get().getCompanyName());

    }
    @Test
    void getStockByTicker_unknownTickerSymbol_returnsEmptyNotNull(){
        Optional<Stock> result = stockDao.getStockByTicker("zzzz");
        assertTrue(result.isEmpty());

    }

    @Test
    void addStock_newStock_getsAnIdAndCanBeFound(){
        //create a new stock object
        Stock tesla = new Stock();
        tesla.setTickerSymbol("TSLA");
        tesla.setCompanyName("Tesla Inc");
//add the test conditions then save it in another object
        Stock saved = stockDao.addStock(tesla);
        Optional<Stock> result = stockDao.getStockById(saved.getStockId());

        assertTrue(saved.getStockId()>0);
        assertEquals("TSLA", result.get().getTickerSymbol());
        assertEquals("Tesla Inc", result.get().getCompanyName());
    }
    @Test
    void addStock_duplicateTicker_throwsDuplicateKeyException(){
        //build the duplicate object that already exists inside the setup()

        Stock apple = new Stock();
        apple.setTickerSymbol("AAPL");
        apple.setCompanyName("Apple Inc");
        assertThrows(DuplicateKeyException.class, () -> stockDao.addStock(apple));
    }
    @Test
    void addStock_nullTicker_throwsException(){
        Stock nullStock = new Stock();
        nullStock.setCompanyName("Null company");
        nullStock.setTickerSymbol(null);
        // ACT + ASSERT: the lambda hands the insert to assertThrows,
        // which runs it and passes only if the database refuses it
        // with a DataIntegrityViolationException.
        assertThrows(DataIntegrityViolationException.class, () -> stockDao.addStock(nullStock));


    }

}
