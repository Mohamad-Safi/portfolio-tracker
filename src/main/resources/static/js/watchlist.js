// watchlist.js — stocks the user watches.
//   GET /api/watchlist  → the user's watchlist items
// ⚠️ Check this URL against WatchListController before the demo.
// A WatchListItem only stores a stockId, so we look the ticker up from the stock list.

function showWatchlist() {
    clearMessage();
    showView('<section class="section"><h2>Watchlist</h2><p>Loading watchlist...</p></section>', 'watchlistButton');

    $.when(loadStocks(), $.getJSON('/api/watchlist'))
        .done(function (stocks, watchlistResult) {
            const items = watchlistResult[0];
            let rows = '';
            if (items.length === 0) {
                rows = '<tr><td colspan="2">Your watchlist is empty.</td></tr>';
            } else {
                $.each(items, function (index, item) {
                    // Depending on the model's getter name, the id arrives as "stockId" or "stock".
                    const stockId = item.stockId !== undefined ? item.stockId : item.stock;
                    const stock = stocks.find(s => s.stockId === stockId);
                    rows += `
                        <tr>
                            <td>${esc(stock ? stock.tickerSymbol : 'Stock ' + stockId)}</td>
                            <td>${esc(stock ? stock.companyName : '')}</td>
                        </tr>`;
                });
            }
            showView(`
                <h2>Watchlist</h2>
                <section class="section">
                    <table>
                        <thead><tr><th>Ticker</th><th>Company</th></tr></thead>
                        <tbody>${rows}</tbody>
                    </table>
                </section>
            `, 'watchlistButton');
        })
        .fail(function (xhr) {
            showView('<section class="section"><h2>Watchlist</h2></section>', 'watchlistButton');
            showMessage(errorText(xhr, 'Failed to load watchlist.'), true);
        });
}
