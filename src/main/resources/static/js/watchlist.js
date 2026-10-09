
function showWatchlist() {
    clearMessage();
    showView('<section class="section"><h2>Watchlist</h2><p>Loading watchlist...</p></section>', 'watchlistButton');

    $.when(loadStocks(), $.getJSON('/api/watchlist'))
        .done(function (stocks, watchlistResult) {
            const items = watchlistResult[0];

            let rows = '';
            if (items.length === 0) {
                rows = '<tr><td colspan="4">Your watchlist is empty. Add stocks from the Stocks page.</td></tr>';
            } else {
                $.each(items, function (index, item) {
                    const stockId = item.stockId !== undefined ? item.stockId : item.stock;
                    const stock = stocks.find(s => s.stockId === stockId);
                    rows += `
                        <tr>
                            <td>${esc(stock ? stock.tickerSymbol : 'Stock ' + stockId)}</td>
                            <td>${esc(stock ? stock.companyName : '')}</td>
                            <td class="num" id="watch-price-${stockId}">—</td>
                            <td>
                                <button class="secondary watch-price-button" data-id="${stockId}">Get live price</button>
                                <button class="danger remove-watch-button" data-id="${stockId}">Remove</button>
                            </td>
                        </tr>`;
                });
            }

            showView(`
                <h2>Watchlist</h2>
                <section class="section">
                    <table>
                        <thead><tr><th>Ticker</th><th>Company</th><th class="num">Live price</th><th></th></tr></thead>
                        <tbody>${rows}</tbody>
                    </table>
                </section>
            `, 'watchlistButton');

            // LIVE PRICE for one watched stock → GET /api/v1/stocks/{id}/price
            $('.watch-price-button').on('click', function () {
                const stockId = $(this).data('id');
                const cell = $('#watch-price-' + stockId);
                cell.text('Loading...');
                $.getJSON('/api/v1/stocks/' + stockId + '/price')
                    .done(function (p) { cell.text(money(p.price)); })
                    .fail(function (xhr) {
                        cell.text('Unavailable');
                        showMessage(errorText(xhr, 'Price unavailable.'), true);
                    });
            });

            $('.remove-watch-button').on('click', function () {
                const stockId = $(this).data('id');
                const ticker = tickerFor(stockId);
                if (!confirm('Remove ' + ticker + ' from your watchlist?')) return;
                apiSend('DELETE', '/api/watchlist/' + stockId)
                    .done(function () {
                        showWatchlist();
                        showMessage(ticker + ' removed from your watchlist.', false);
                    })
                    .fail(function (xhr) { showMessage(errorText(xhr, 'Could not remove it.'), true); });
            });
        })
        .fail(function (xhr) {
            showView('<section class="section"><h2>Watchlist</h2></section>', 'watchlistButton');
            showMessage(errorText(xhr, 'Failed to load watchlist.'), true);
        });
}