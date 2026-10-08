package com.portfoliotracker.mapper;

import com.portfoliotracker.model.WatchListItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WatchListMapper implements RowMapper<WatchListItem> {

    @Override
    public WatchListItem mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        WatchListItem item = new WatchListItem();

        item.setWatchListItemId(rs.getInt("WatchListItemId"));
        item.setUserId(rs.getInt("userId"));
        item.setStockId(rs.getInt("stockId"));
        item.setAddedAt(
                rs.getTimestamp("addedAt").toLocalDateTime()
        );

        return item;
    }
}