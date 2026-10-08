package com.portfoliotracker.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import com.portfoliotracker.mapper.WatchListMapper;
import com.portfoliotracker.model.WatchListItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WatchListDaoImpl implements WatchListDao {

    private final JdbcTemplate jdbcTemplate;

    public WatchListDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WatchListItem addWatchListItem(WatchListItem item) {

        final String sql = "INSERT INTO watchlist_item (userId, stockId) " +
                     "VALUES (?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection conn) -> {

            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getUserId());
            statement.setInt(2, item.getStockId());

            return statement;
        }, keyHolder);

        item.setWatchListItemId(keyHolder.getKey().intValue());

        return item;
    }

    @Override
    public List<WatchListItem> getWatchListByUserId(int userId) {

        final String sql = "SELECT * FROM watchlist_item WHERE userId = ?";

        return jdbcTemplate.query(sql, new WatchListMapper(), userId);
    }

    @Override
    public WatchListItem findWatchListItem(int userId, int stockId) {

        final String sql = "SELECT * FROM watchlist_item " +
                           "WHERE userId = ? AND stockId = ?";

        return jdbcTemplate.queryForObject(sql, new WatchListMapper(), userId, stockId);
    }

    @Override
    public void removeWatchListItem(int userId, int stockId) {
       final String sql = "DELETE FROM watchlist_item" +
                          "WHERE userId = ? AND stockId = ?";
    }
}