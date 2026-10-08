package com.portfoliotracker.service;

import com.portfoliotracker.dto.PortfolioSummaryDto;


//build users full portfolio
//never null
public interface PortfolioService {

    PortfolioSummaryDto getPortfolioSummary(int userId);
}
