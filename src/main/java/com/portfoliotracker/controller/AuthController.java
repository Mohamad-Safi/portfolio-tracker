package com.portfoliotracker.controller;

import com.portfoliotracker.dto.RegisterRequestDto;
import com.portfoliotracker.dto.UserResponseDto;
import com.portfoliotracker.model.UserAccount;
import com.portfoliotracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        UserAccount user = authService.register(request);
        return UserResponseDto.from(user);
    }
}
