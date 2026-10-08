function showStocks() {
    clearMessage();
    showView('<section class="section"><h2>Stocks</h2><p>Loading stocks...</p></section>', 'stocksButton');

    loadStocks()
        .done(function (stocks) {
            let rows = '';
            $.each(stocks, function (index, stock) {
                rows += `
                    <tr>
                        <td>${esc(stock.tickerSymbol)}</td>
                        <td>${esc(stock.companyName)}</td>
                        <td class="num" id="price-${stock.stockId}">—</td>
                        <td>
                            <button class="secondary price-button" data-id="${stock.stockId}">Get live price</button>
                            <button class="watch-button" data-id="${stock.stockId}">+ Watchlist</button>
                        </td>
                    </tr>`;
            });

            showView(`
                <h2>Stocks</h2>
                <p class="muted">Live prices come from Twelve Data and are cached for 15 minutes.</p>
                <section class="section">
                    <table>
                        <thead><tr><th>Ticker</th><th>Company</th><th class="num">Live price</th><th></th></tr></thead>
                        <tbody>${rows}</tbody>
                    </table>
                </section>
            `, 'stocksButton');

            $('.price-button').on('click', function () {
                const stockId = $(this).data('id');
                const cell = $('#price-' + stockId);
                cell.text('Loading...');
                $.getJSON('/api/v1/stocks/' + stockId + '/price')
                    .done(function (p) { cell.text(money(p.price)); })
                    .fail(function (xhr) {
                        cell.text('Unavailable');
                        showMessage(errorText(xhr, 'Price unavailable.'), true);
                    });
            });

            $('.watch-button').on('click', function () {
                const stockId = $(this).data('id');
                apiSend('POST', '/api/watchlist', { stockId: stockId })
                    .done(function () {
                        showMessage(tickerFor(stockId) + ' added to your watchlist.', false);
                    })
                    .fail(function (xhr) {
                        showMessage(errorText(xhr, 'Could not add to watchlist.'), true);
                    });
            });
        })
        .fail(function (xhr) {
            showView('<section class="section"><h2>Stocks</h2></section>', 'stocksButton');
            showMessage(errorText(xhr, 'Failed to load stocks.'), true);
        });
}