package com.portfoliotracker.controller;


import com.portfoliotracker.dto.PortfolioSummaryDto;
import com.portfoliotracker.security.AuthInterceptor;
import com.portfoliotracker.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService){
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public PortfolioSummaryDto getPortfolio(
            @SessionAttribute(AuthInterceptor.USER_ID) Integer userId
    ){
        return portfolioService.getPortfolioSummary(userId);
    }
}
