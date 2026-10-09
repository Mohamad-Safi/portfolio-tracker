// portfolio.js — the portfolio summary: GET /api/v1/portfolio
// The server already did all the maths (cost, value, gain, totals);
// this file only DISPLAYS the PortfolioSummaryDto it returns.
// The ☰ menu on each row opens a details panel (helpers openPanel, panelHeader,
// tile and closeRowMenus live in holdings.js and are shared).

let portfolioPositions = [];   // the positions from the last load, used by "View details"

function showPortfolio() {
    clearMessage();
    openPanelKey = null;
    showView('<h2>Portfolio</h2><section class="section"><p>Loading portfolio...</p></section>', 'portfolioButton');

    $.getJSON('/api/v1/portfolio')
        .done(function (p) {
            portfolioPositions = p.positions;

            let rows = '';
            if (p.positions.length === 0) {
                rows = '<tr><td colspan="5" class="empty">Your portfolio is empty. Add your first stock on the Holdings page.</td></tr>';
            }
            $.each(p.positions, function (index, pos) {
                rows += `
                    <tr class="position-row" data-index="${index}">
                        <td>
                            <div class="symbol">${esc(pos.tickerSymbol)}</div>
                            <div class="company">${esc(pos.companyName)}</div>
                        </td>
                        <td class="num">${esc(Number(pos.quantity))}</td>
                        <td class="num">${pos.marketValue === null ? '<span class="muted">Price unavailable</span>' : money(pos.marketValue)}</td>
                        <td class="num">
                            ${gainHtml(pos.gain, money(pos.gain))}
                            <div class="sub-value">${gainHtml(pos.gainPercent, percent(pos.gainPercent))}</div>
                        </td>
                        <td class="actions">
                            <div class="row-menu">
                                <button class="icon-button row-menu-button" aria-label="Actions">☰</button>
                                <div class="row-menu-list" hidden>
                                    <button class="row-action" data-action="details">View details</button>
                                    <button class="row-action" data-action="manage">Manage in Holdings</button>
                                </div>
                            </div>
                        </td>
                    </tr>`;
            });

            showView(`
                <h2>Portfolio</h2>
                <div class="totals">
                    <div><span>Invested</span><strong>${money(p.totalCost)}</strong></div>
                    <div><span>Current value</span><strong>${money(p.totalValue)}</strong></div>
                    <div><span>Total gain</span>
                        <strong>${gainHtml(p.totalGain, money(p.totalGain))}</strong>
                        <em class="total-percent">${gainHtml(p.totalGainPercent, percent(p.totalGainPercent))}</em>
                    </div>
                </div>
                ${p.allPricesAvailable ? '' : '<p class="muted">Some live prices are unavailable right now, so totals only include stocks with a price.</p>'}
                <section class="section">
                    <table id="portfolioTable">
                        <thead>
                            <tr><th>Stock</th><th class="num">Shares</th><th class="num">Value</th><th class="num">Gain</th><th></th></tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </section>
            `, 'portfolioButton');

            wirePortfolioMenus();
        })
        .fail(function (xhr) {
            showView('<h2>Portfolio</h2>', 'portfolioButton');
            showMessage(errorText(xhr, 'Failed to load portfolio.'), true);
        });
}

function wirePortfolioMenus() {
    const table = $('#portfolioTable');

    // open / close one row's ☰ menu
    table.on('click', '.row-menu-button', function (event) {
        event.stopPropagation();
        const list = $(this).siblings('.row-menu-list');
        const wasHidden = list.prop('hidden');
        closeRowMenus();
        list.prop('hidden', !wasHidden);
    });

    // a choice in the menu
    table.on('click', '.row-action', function (event) {
        event.stopPropagation();
        const action = $(this).data('action');
        const row = $(this).closest('tr');
        closeRowMenus();
        if (action === 'details') showPositionDetails(row, row.data('index'));
        if (action === 'manage') showHoldings();
    });

    // ✕ inside a panel
    table.on('click', '.close-panel', function () { closePanel(); });
}

// All the numbers already came from the server, so no extra request is needed.
function showPositionDetails(row, index) {
    const pos = portfolioPositions[index];
    openPanel(row, 'p' + index + ':details', `
        ${panelHeader(pos.tickerSymbol, pos.companyName)}
        <div class="detail-grid">
            ${tile('Shares', esc(Number(pos.quantity)))}
            ${tile('Avg price paid', money(pos.averagePurchasePrice))}
            ${tile('Invested', money(pos.cost))}
            ${tile('Live price', pos.currentPrice === null ? 'Unavailable' : money(pos.currentPrice))}
            ${tile('Current value', money(pos.marketValue))}
            ${tile('Gain / loss', gainHtml(pos.gain, money(pos.gain)))}
            ${tile('Return', gainHtml(pos.gainPercent, percent(pos.gainPercent)))}
        </div>
    `);
}
