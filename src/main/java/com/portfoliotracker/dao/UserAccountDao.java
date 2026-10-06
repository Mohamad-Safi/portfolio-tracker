package com.portfoliotracker.dao;

import com.portfoliotracker.model.UserAccount;

public interface UserAccountDao {

    UserAccount createUser(UserAccount user);

    UserAccount findByUsername(String username);

    UserAccount findByEmail(String email);

    UserAccount findById(int userId);
}
