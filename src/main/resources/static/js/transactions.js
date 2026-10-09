

//logged in uers/buy sell history

function showTransactions() {
    clearMessage();
    showView('<section class="section"><h2>Transactions</h2><p>Loading transactions...</p></section>', 'transactionsButton');

    $.when(loadStocks(), $.getJSON('/api/transactions'))
        .done(function (stocks, transactionsResult) {
            const transactions = transactionsResult[0];

            let rows = '';
            if (transactions.length === 0) {
                rows = '<tr><td colspan="6">No transactions yet.</td></tr>';
            } else {
                $.each(transactions, function (index, t) {
                    const total = Number(t.quantity) * Number(t.price);
                    rows += `
                        <tr>
                            <td>${esc(formatDate(t.transactionDate))}</td>
                            <td>${esc(t.transactionType)}</td>
                            <td>${esc(tickerFor(t.stockId))}</td>
                            <td class="num">${esc(Number(t.quantity))}</td>
                            <td class="num">${money(t.price)}</td>
                            <td class="num">${money(total)}</td>
                        </tr>`;
                });
            }

            showView(`
                <h2>Transactions</h2>
                <section class="section">
                    <table>
                        <thead>
                            <tr><th>Date</th><th>Type</th><th>Stock</th>
                                <th class="num">Qty</th><th class="num">Price</th><th class="num">Total</th></tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </section>
            `, 'transactionsButton');
        })
        .fail(function (xhr) {
            showView('<section class="section"><h2>Transactions</h2></section>', 'transactionsButton');
            showMessage(errorText(xhr, 'Failed to load transactions.'), true);
        });
}

// "2026-10-08T17:22:10" → "8 Oct 2026, 17:22"
function formatDate(value) {
    if (!value) return '—';
    return new Date(value).toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' });
}