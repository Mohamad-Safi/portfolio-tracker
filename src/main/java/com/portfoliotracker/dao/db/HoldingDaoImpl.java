package com.portfoliotracker.dao.db;

import com.portfoliotracker.mapper.HoldingMapper;
import com.portfoliotracker.model.Holding;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class HoldingDaoImpl implements HoldingDao{

    private final JdbcTemplate jdbcTemplate;

    public HoldingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Holding createHolding(Holding holding) {

        String sql = "INSERT INTO holding(userId, stockId, quantity, averagePurchasePrice) " +
                     "VALUES(?,?,?,?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection conn) -> {

            PreparedStatement statement = conn.prepareStatement(
                    sql, PreparedStatement.RETURN_GENERATED_KEYS);


            statement.setInt(1, holding.getUserId());
            statement.setInt(2, holding.getStockId());
            statement.setBigDecimal(3, holding.getQuantity());
            statement.setBigDecimal(4, holding.getAveragePurchasePrice());
            return statement;
        }, keyHolder);

        holding.setHoldingId(keyHolder.getKey().intValue());

        return holding;

    }

    @Override
    public List<Holding> getHoldingsByUserId(int userId) {

        String sql = "SELECT * FROM holding WHERE userId = ?";

        return jdbcTemplate.query(sql, new HoldingMapper(), userId);
    }

    @Override
    public Holding findHoldingById(int holdingId) {

        String sql = "SELECT * FROM holding WHERE holdingId = ?";

        return jdbcTemplate.queryForObject(sql, new HoldingMapper(), holdingId);
    }

    @Override
    public Holding findHoldingByUserAndStock(int userId, int stockId) {

        String sql = "SELECT * FROM holding WHERE " +
                     "userId = ? AND stockId = ?";

        return jdbcTemplate.queryForObject(sql, new HoldingMapper(), userId, stockId);
    }

    @Override
    public void updateHolding(Holding holding) {

        String sql = "UPDATE holding " +
                "SET quantity = ?, averagePurchasePrice = ? " +
                "WHERE holdingId = ?";

        jdbcTemplate.update(sql, holding.getQuantity(), holding.getAveragePurchasePrice(),
                                 holding.getHoldingId());
    }

    @Override
    public void deleteHolding(int holdingId) {

        String sql = "DELETE FROM holding WHERE holdingId = ?";
        jdbcTemplate.update(sql, holdingId);
    }

}
