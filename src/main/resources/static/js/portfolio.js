// portfolio.js — the portfolio summary: GET /api/v1/portfolio
// The server already did all the maths (cost, value, gain, totals);
// this file only DISPLAYS the PortfolioSummaryDto it returns.

function showPortfolio() {
    clearMessage();
    showView('<section class="section"><h2>Portfolio</h2><p>Loading portfolio...</p></section>', 'portfolioButton');

    $.getJSON('/api/v1/portfolio')
        .done(function (p) {
            let rows = '';
            if (p.positions.length === 0) {
                rows = '<tr><td colspan="8">Your portfolio is empty. Add a stock under Holdings.</td></tr>';
            } else {
                $.each(p.positions, function (index, pos) {
                    rows += `
                        <tr>
                            <td>${esc(pos.tickerSymbol)} — ${esc(pos.companyName)}</td>
                            <td class="num">${esc(Number(pos.quantity))}</td>
                            <td class="num">${money(pos.averagePurchasePrice)}</td>
                            <td class="num">${pos.currentPrice === null ? 'Price unavailable' : money(pos.currentPrice)}</td>
                            <td class="num">${money(pos.cost)}</td>
                            <td class="num">${money(pos.marketValue)}</td>
                            <td class="num">${gainHtml(pos.gain, money(pos.gain))}</td>
                            <td class="num">${gainHtml(pos.gainPercent, percent(pos.gainPercent))}</td>
                        </tr>`;
                });
            }

            showView(`
                <h2>Portfolio</h2>
                <div class="totals">
                    <div><span>Total cost</span><strong>${money(p.totalCost)}</strong></div>
                    <div><span>Market value</span><strong>${money(p.totalValue)}</strong></div>
                    <div><span>Gain / loss</span><strong>${gainHtml(p.totalGain, money(p.totalGain))}</strong></div>
                    <div><span>Gain %</span><strong>${gainHtml(p.totalGainPercent, percent(p.totalGainPercent))}</strong></div>
                </div>
                ${p.allPricesAvailable ? '' : '<p class="muted">Some live prices are unavailable right now, so totals only include stocks with a price.</p>'}
                <section class="section">
                    <table>
                        <thead>
                            <tr>
                                <th>Stock</th><th class="num">Qty</th><th class="num">Avg price</th>
                                <th class="num">Live price</th><th class="num">Cost</th><th class="num">Value</th>
                                <th class="num">Gain</th><th class="num">Gain %</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </section>
            `, 'portfolioButton');
        })
        .fail(function (xhr) {
            showView('<section class="section"><h2>Portfolio</h2></section>', 'portfolioButton');
            showMessage(errorText(xhr, 'Failed to load portfolio.'), true);
        });
}
