// holdings.js — full CRUD on the logged-in user's holdings.
//   GET    /api/holdings          → list (the server knows who you are from the session)
//   GET    /api/holdings/{id}     → one holding
//   POST   /api/holdings          → add     body: {stockId, quantity, purchasePrice}
//   PUT    /api/holdings/{id}     → update  body: {quantity, averagePurchasePrice}
//   DELETE /api/holdings/{id}     → delete

let editingHoldingId = null;   // null = the form adds; a number = the form edits that holding

function showHoldings() {
    clearMessage();
    editingHoldingId = null;
    showView('<section class="section"><h2>Holdings</h2><p>Loading holdings...</p></section>', 'holdingsButton');

    // We need the stock list (for the dropdown and tickers) and the holdings.
    $.when(loadStocks(), $.getJSON('/api/holdings'))
        .done(function (stocks, holdingsResult) {
            const holdings = holdingsResult[0];   // $.when wraps ajax results in an array
            drawHoldings(stocks, holdings);
        })
        .fail(function (xhr) {
            showView('<section class="section"><h2>Holdings</h2></section>', 'holdingsButton');
            showMessage(errorText(xhr, 'Failed to load holdings.'), true);
        });
}

function drawHoldings(stocks, holdings) {
    let options = '';
    $.each(stocks, function (index, s) {
        options += `<option value="${s.stockId}">${esc(s.tickerSymbol)} — ${esc(s.companyName)}</option>`;
    });

    let rows = '';
    if (holdings.length === 0) {
        rows = '<tr><td colspan="5">No holdings yet. Add one above.</td></tr>';
    } else {
        $.each(holdings, function (index, h) {
            rows += `
                <tr>
                    <td>${esc(h.holdingId)}</td>
                    <td>${esc(tickerFor(h.stockId))}</td>
                    <td class="num">${esc(Number(h.quantity))}</td>
                    <td class="num">${money(h.averagePurchasePrice)}</td>
                    <td>
                        <button class="secondary view-button" data-id="${h.holdingId}">View</button>
                        <button class="secondary edit-button" data-id="${h.holdingId}"
                                data-stock="${h.stockId}" data-quantity="${Number(h.quantity)}"
                                data-price="${Number(h.averagePurchasePrice)}">Edit</button>
                        <button class="danger delete-button" data-id="${h.holdingId}">Delete</button>
                    </td>
                </tr>`;
        });
    }

    showView(`
        <h2>Holdings</h2>
        <section class="section">
            <h2 id="holdingFormTitle">Add a holding</h2>
            <form id="holdingForm" class="inline-form" novalidate>
                <div class="field"><label for="holdingStock">Stock</label><select id="holdingStock">${options}</select></div>
                <div class="field"><label for="holdingQuantity">Quantity</label><input id="holdingQuantity" type="number" step="0.000001" min="0"></div>
                <div class="field"><label for="holdingPrice">Price per share</label><input id="holdingPrice" type="number" step="0.0001" min="0"></div>
                <button type="submit" id="holdingSubmit">Add</button>
                <button type="button" id="cancelEdit" class="secondary" hidden>Cancel</button>
            </form>
        </section>
        <section class="section">
            <table>
                <thead><tr><th>Id</th><th>Stock</th><th class="num">Qty</th><th class="num">Avg price</th><th>Actions</th></tr></thead>
                <tbody>${rows}</tbody>
            </table>
            <p class="muted">Changes show up straight away on the Portfolio page, with live prices.</p>
        </section>
    `, 'holdingsButton');

    // CREATE (POST) or UPDATE (PUT)
    $('#holdingForm').on('submit', function (event) {
        event.preventDefault();
        const stockId = Number($('#holdingStock').val());
        const quantity = Number($('#holdingQuantity').val());
        const price = Number($('#holdingPrice').val());

        if (!(quantity > 0)) return showMessage('Quantity must be more than 0.', true);
        if (!(price > 0)) return showMessage('Price must be more than 0.', true);

        const request = editingHoldingId === null
            ? apiSend('POST', '/api/holdings', { stockId: stockId, quantity: quantity, purchasePrice: price })
            : apiSend('PUT', '/api/holdings/' + editingHoldingId, { quantity: quantity, averagePurchasePrice: price });
        const doneText = editingHoldingId === null ? 'Holding added.' : 'Holding ' + editingHoldingId + ' updated.';

        request
            .done(function () { showHoldings(); showMessage(doneText, false); })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not save the holding.'), true); });
    });

    // READ ONE
    $('.view-button').on('click', function () {
        const id = $(this).data('id');
        $.getJSON('/api/holdings/' + id)
            .done(function (h) {
                showMessage('Holding ' + h.holdingId + ': ' + Number(h.quantity) + ' × ' + tickerFor(h.stockId)
                    + ' at an average of ' + money(h.averagePurchasePrice), false);
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not load that holding.'), true); });
    });

    // Switch the form to EDIT mode
    $('.edit-button').on('click', function () {
        const b = $(this);
        editingHoldingId = b.data('id');
        $('#holdingStock').val(b.data('stock')).prop('disabled', true);
        $('#holdingQuantity').val(b.data('quantity'));
        $('#holdingPrice').val(b.data('price'));
        $('#holdingFormTitle').text('Edit holding ' + editingHoldingId);
        $('#holdingSubmit').text('Save');
        $('#cancelEdit').prop('hidden', false);
    });

    $('#cancelEdit').on('click', function () {
        editingHoldingId = null;
        $('#holdingForm')[0].reset();
        $('#holdingStock').prop('disabled', false);
        $('#holdingFormTitle').text('Add a holding');
        $('#holdingSubmit').text('Add');
        $(this).prop('hidden', true);
    });

    // DELETE
    $('.delete-button').on('click', function () {
        const id = $(this).data('id');
        if (!confirm('Delete holding ' + id + '?')) return;
        apiSend('DELETE', '/api/holdings/' + id)
            .done(function () { showHoldings(); showMessage('Holding ' + id + ' deleted.', false); })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not delete the holding.'), true); });
    });
}
