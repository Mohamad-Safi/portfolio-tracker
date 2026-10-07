package com.portfoliotracker.mapper;

import com.portfoliotracker.model.UserAccount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccountMapper implements RowMapper<UserAccount> {

    @Override
    public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserAccount user = new UserAccount();

        user.setUserId(rs.getInt("userId"));
        user.setUserName(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("passwordHash"));
        user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

        return user;
    }
}
