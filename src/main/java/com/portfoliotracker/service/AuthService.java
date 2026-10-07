package com.portfoliotracker.service;

import com.portfoliotracker.dao.UserAccountDao;
import com.portfoliotracker.dto.RegisterRequestDto;
import com.portfoliotracker.exception.EmailTakenException;
import com.portfoliotracker.exception.UsernameTakenException;
import com.portfoliotracker.model.UserAccount;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserAccountDao userAccountDao;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountDao userAccountDao, PasswordEncoder passwordEncoder) {
        this.userAccountDao = userAccountDao;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount register(RegisterRequestDto request) {

        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();

        if (userAccountDao.findByUsername(username) != null) {
            throw new UsernameTakenException();
        }

        if (userAccountDao.findByEmail(email) != null) {
            throw new EmailTakenException();
        }

        UserAccount user = new UserAccount();
        user.setUserName(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        try {
            return userAccountDao.createUser(user);
        } catch (DuplicateKeyException e) {
            throw new UsernameTakenException();
        }
    }
}
