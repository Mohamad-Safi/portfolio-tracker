package com.portfoliotracker.dao;

import com.portfoliotracker.model.Holding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"local", "test"})
public class HoldingDaoImplTest {

    @Autowired
    private HoldingDao holdingDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update("""
        INSERT IGNORE INTO user_account
        (userId, username, email, passwordHash)
        VALUES (1, 'holding_test_user',
                'holding_test@example.com', 'test_hash')
        """);

        jdbcTemplate.update("""
        INSERT IGNORE INTO stock
        (stockId, tickerSymbol, companyName)
        VALUES (1, 'AAPL', 'Apple Inc.')
        """);
    }

    @Test
    void createAndFindHolding() {

        Holding holding = new Holding();
        holding.setUserId(1);
        holding.setStockId(1);
        holding.setQuantity(new BigDecimal("10"));
        holding.setAveragePurchasePrice(new BigDecimal("150.00"));

        Holding createdHolding = holdingDao.createHolding(holding);
        assertTrue(createdHolding.getHoldingId() > 0);

        Holding foundHolding = holdingDao.findHoldingById(createdHolding.getHoldingId());

        assertNotNull(foundHolding);
        assertEquals(createdHolding.getHoldingId(), foundHolding.getHoldingId());
        assertEquals(1, foundHolding.getUserId());
        assertEquals(1, foundHolding.getStockId());
        assertEquals(0, new BigDecimal("10").compareTo(foundHolding.getQuantity()));
        assertEquals(0, new BigDecimal("150.00").compareTo(foundHolding.getAveragePurchasePrice()));
        holdingDao.deleteHolding(createdHolding.getHoldingId());
    }

    @Test
    void getHoldingsByUserId() {

        Holding holding = new Holding();
        holding.setUserId(1);
        holding.setStockId(1);
        holding.setQuantity(new BigDecimal("10"));
        holding.setAveragePurchasePrice(new BigDecimal("150.00"));

        Holding createdHolding = holdingDao.createHolding(holding);

        List<Holding> holdings = holdingDao.getHoldingsByUserId(1);

        assertNotNull(holdings);
        assertFalse(holdings.isEmpty());

        boolean found = holdings.stream().anyMatch(h -> h.getHoldingId() == createdHolding.getHoldingId());
        assertTrue(found);

        holdingDao.deleteHolding(createdHolding.getHoldingId());
    }

    @Test
    void findHoldingByUserAndStock() {

        Holding holding = new Holding();
        holding.setUserId(1);
        holding.setStockId(1);
        holding.setQuantity(new BigDecimal("5"));
        holding.setAveragePurchasePrice(new BigDecimal("100.00"));

        Holding createdHolding = holdingDao.createHolding(holding);

        Holding foundHolding = holdingDao.findHoldingByUserAndStock(1, 1);

        assertNotNull(foundHolding);
        assertEquals(createdHolding.getHoldingId(), foundHolding.getHoldingId());
        assertEquals(0, new BigDecimal("5").compareTo(foundHolding.getQuantity()));
        assertEquals(0, new BigDecimal("100.00").compareTo(foundHolding.getAveragePurchasePrice()));

        holdingDao.deleteHolding(createdHolding.getHoldingId());
    }


    @Test
    void updateHolding() {

        Holding holding = new Holding();
        holding.setUserId(1);
        holding.setStockId(1);
        holding.setQuantity(new BigDecimal("10"));
        holding.setAveragePurchasePrice(new BigDecimal("150.00"));

        Holding createdHolding = holdingDao.createHolding(holding);

        createdHolding.setQuantity(new BigDecimal("20"));
        createdHolding.setAveragePurchasePrice(new BigDecimal("175.00"));

        holdingDao.updateHolding(createdHolding);

        Holding updatedHolding = holdingDao.findHoldingById(createdHolding.getHoldingId());

        assertEquals(0, new BigDecimal("20").compareTo(updatedHolding.getQuantity()));
        assertEquals(0, new BigDecimal("175.00").compareTo(updatedHolding.getAveragePurchasePrice()));

        holdingDao.deleteHolding(createdHolding.getHoldingId());
    }

    @Test
    void findHoldingByUserAndStockReturnsNullWhenNotFound() {

        Holding holding = holdingDao.findHoldingByUserAndStock(1, 999);

        assertNull(holding);
    }

}