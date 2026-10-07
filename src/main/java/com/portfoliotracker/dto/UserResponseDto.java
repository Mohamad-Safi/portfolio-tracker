package com.portfoliotracker.dto;

import com.portfoliotracker.model.UserAccount;

public record UserResponseDto(
        int userId,
        String username,
        String email
) {

    public static UserResponseDto from(UserAccount user) {
        return new UserResponseDto(user.getUserId(), user.getUsername(), user.getEmail());
    }
}
