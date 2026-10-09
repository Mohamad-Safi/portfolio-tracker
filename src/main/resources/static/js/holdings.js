// holdings.js — your positions, managed like a trading platform.
//   GET    /api/holdings                → all positions
//   POST   /api/holdings                → open a position   body: {stockId, quantity, purchasePrice}
//   PUT    /api/holdings/{id}           → edit a position   body: {quantity, averagePurchasePrice}
//   DELETE /api/holdings/{id}           → close a position
//   POST   /api/v1/stocks               → add a stock by ticker   body: {tickerSymbol}
//   GET    /api/v1/stocks/{id}/price    → live price

let holdingsById = {};       // holdingId → holding, refilled every time the page is drawn
let openPanelKey = null;     // which panel is open, e.g. "6:edit" (null = none) — shared with portfolio.js
let openTicketFor = null;    // after adding a new stock, open the ticket with it selected

// ── Load and draw the page ────────────────────────────────────────────────
function showHoldings() {
    clearMessage();
    openPanelKey = null;
    showView('<h2>Holdings</h2><section class="section"><p>Loading holdings...</p></section>', 'holdingsButton');

    $.when(loadStocks(), $.getJSON('/api/holdings'))
        .done(function (stocks, holdingsResult) {
            drawHoldings(stocks, holdingsResult[0]);
        })
        .fail(function (xhr) {
            showView('<h2>Holdings</h2>', 'holdingsButton');
            showMessage(errorText(xhr, 'Failed to load holdings.'), true);
        });
}

function drawHoldings(stocks, holdings) {
    // One table row per position
    holdingsById = {};
    let rows = '';
    if (holdings.length === 0) {
        rows = '<tr><td colspan="5" class="empty">No positions yet. Click "New position" to buy your first stock.</td></tr>';
    }
    $.each(holdings, function (index, h) {
        holdingsById[h.holdingId] = h;
        const stock = stocks.find(s => s.stockId === h.stockId);
        const cost = Number(h.quantity) * Number(h.averagePurchasePrice);
        rows += `
            <tr class="position-row" data-id="${h.holdingId}">
                <td>
                    <div class="symbol">${esc(stock ? stock.tickerSymbol : 'Stock ' + h.stockId)}</div>
                    <div class="company">${esc(stock ? stock.companyName : '')}</div>
                </td>
                <td class="num">${esc(Number(h.quantity))}</td>
                <td class="num">${money(h.averagePurchasePrice)}</td>
                <td class="num">${money(cost)}</td>
                <td class="actions">
                    <div class="row-menu">
                        <button class="icon-button row-menu-button" aria-label="Actions">☰</button>
                        <div class="row-menu-list" hidden>
                            <button class="row-action" data-action="edit">Edit position</button>
                            <button class="row-action danger-text" data-action="close">Close position</button>
                        </div>
                    </div>
                </td>
            </tr>`;
    });

    // The stock dropdown for the order ticket
    let options = '';
    $.each(stocks, function (index, s) {
        options += `<option value="${s.stockId}">${esc(s.tickerSymbol)} — ${esc(s.companyName)}</option>`;
    });

    showView(`
        <div class="page-header">
            <h2>Holdings</h2>
            <button id="newPositionButton">+ New position</button>
        </div>

        <section class="section order-ticket" id="orderTicket" hidden>
            <h2>New position</h2>
            <form id="orderForm" novalidate>
                <div class="ticket-grid">
                    <div class="field"><label for="orderStock">Stock</label><select id="orderStock">${options}</select></div>
                    <div class="field"><label for="orderQuantity">Quantity</label><input id="orderQuantity" type="number" min="0" step="0.000001" placeholder="0"></div>
                    <div class="field"><label for="orderPrice">Price per share</label><input id="orderPrice" type="number" min="0" step="0.0001" placeholder="0.00"></div>
                    <div class="field"><label>Estimated total</label><div class="ticket-total" id="orderTotal">$0.00</div></div>
                </div>
                <div class="ticket-buttons">
                    <button type="submit">Buy</button>
                    <button type="button" class="secondary" id="cancelOrder">Cancel</button>
                    <a href="#" class="text-link" id="showAddTicker">Stock not listed? Add it by ticker</a>
                </div>
            </form>
            <form id="tickerForm" class="inline-form ticker-form" hidden novalidate>
                <div class="field"><label for="newTicker">Ticker</label><input id="newTicker" placeholder="e.g. TSLA"></div>
                <button type="submit" class="secondary">Add stock</button>
            </form>
        </section>

        <section class="section">
            <table id="holdingsTable">
                <thead>
                    <tr><th>Stock</th><th class="num">Qty</th><th class="num">Avg price</th><th class="num">Cost</th><th></th></tr>
                </thead>
                <tbody>${rows}</tbody>
            </table>
        </section>
    `, 'holdingsButton');

    wireOrderTicket();
    wireRowMenus();

    // Just added a stock by ticker? Re-open the ticket with it selected.
    if (openTicketFor !== null) {
        openTicket(openTicketFor);
        openTicketFor = null;
    }
}

// ── Order ticket (CREATE) ─────────────────────────────────────────────────
function wireOrderTicket() {
    $('#newPositionButton').on('click', function () { openTicket(); });
    $('#cancelOrder').on('click', closeTicket);

    // The estimated total updates on every key press
    $('#orderQuantity, #orderPrice').on('input', updateOrderTotal);

    $('#showAddTicker').on('click', function (event) {
        event.preventDefault();
        $('#tickerForm').prop('hidden', false);
        $('#newTicker').focus();
    });

    // BUY → POST /api/holdings
    $('#orderForm').on('submit', function (event) {
        event.preventDefault();
        const stockId = Number($('#orderStock').val());
        const quantity = Number($('#orderQuantity').val());
        const price = Number($('#orderPrice').val());
        if (!(quantity > 0)) return showMessage('Quantity must be more than 0.', true);
        if (!(price > 0)) return showMessage('Price must be more than 0.', true);

        apiSend('POST', '/api/holdings', { stockId: stockId, quantity: quantity, purchasePrice: price })
            .done(function () {
                showHoldings();
                showMessage('Bought ' + quantity + ' × ' + tickerFor(stockId) + '.', false);
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not open the position.'), true); });
    });

    // ADD A STOCK BY TICKER → POST /api/v1/stocks
    $('#tickerForm').on('submit', function (event) {
        event.preventDefault();
        const ticker = $('#newTicker').val().trim();
        if (!ticker) return showMessage('Enter a ticker, e.g. TSLA.', true);

        apiSend('POST', '/api/v1/stocks', { tickerSymbol: ticker })
            .done(function (stock) {
                stockCache = null;               // forget the old list so the new stock appears
                openTicketFor = stock.stockId;   // reopen the ticket with it selected
                showHoldings();
                showMessage('Added ' + stock.tickerSymbol + ' — ' + stock.companyName + '.', false);
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not add the stock.'), true); });
    });
}

function openTicket(stockId) {
    closePanel();
    $('#orderTicket').prop('hidden', false);
    if (stockId) $('#orderStock').val(stockId);
    $('#orderQuantity').focus();
}

function closeTicket() {
    $('#orderForm')[0].reset();
    $('#tickerForm').prop('hidden', true);
    $('#orderTicket').prop('hidden', true);
    updateOrderTotal();
}

function updateOrderTotal() {
    const total = Number($('#orderQuantity').val()) * Number($('#orderPrice').val());
    $('#orderTotal').text(money(total || 0));
}

// ── Row menus (☰) and the panel under a row ───────────────────────────────
function wireRowMenus() {
    // Event delegation: ONE handler on the table catches clicks from every row,
    // including rows and panels that are added later.
    const table = $('#holdingsTable');

    // Open / close one row's ☰ menu
    table.on('click', '.row-menu-button', function (event) {
        event.stopPropagation();   // stop the document click (bottom of file) closing it again
        const list = $(this).siblings('.row-menu-list');
        const wasHidden = list.prop('hidden');
        closeRowMenus();
        list.prop('hidden', !wasHidden);
    });

    // A choice in the menu
    table.on('click', '.row-action', function (event) {
        event.stopPropagation();
        const action = $(this).data('action');
        const row = $(this).closest('tr');
        const holdingId = row.data('id');
        closeRowMenus();
        if (action === 'edit') showEdit(row, holdingId);
        if (action === 'close') showCloseConfirm(row, holdingId);
    });

    // ✕ / Cancel / Keep buttons inside a panel
    table.on('click', '.close-panel', function () { closePanel(); });

    // EDIT → PUT /api/holdings/{id}
    table.on('submit', '.edit-form', function (event) {
        event.preventDefault();
        const holdingId = $(this).data('id');
        const quantity = Number($(this).find('.edit-quantity').val());
        const price = Number($(this).find('.edit-price').val());
        if (!(quantity > 0)) return showMessage('Quantity must be more than 0.', true);
        if (!(price > 0)) return showMessage('Price must be more than 0.', true);

        apiSend('PUT', '/api/holdings/' + holdingId, { quantity: quantity, averagePurchasePrice: price })
            .done(function () {
                showHoldings();
                showMessage('Position updated.', false);
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not update the position.'), true); });
    });

    // CLOSE POSITION → DELETE /api/holdings/{id}
    table.on('click', '.confirm-close', function () {
        const holdingId = $(this).data('id');
        const ticker = tickerFor(holdingsById[holdingId].stockId);
        apiSend('DELETE', '/api/holdings/' + holdingId)
            .done(function () {
                showHoldings();
                showMessage(ticker + ' position closed.', false);
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Could not close the position.'), true); });
    });
}

function closeRowMenus() {
    $('.row-menu-list').prop('hidden', true);
}

// Opens a panel in a new table row right under the clicked position.
// Only one panel at a time; choosing the same thing again closes it.
function openPanel(row, key, html) {
    const sameAsBefore = openPanelKey === key;
    closePanel();
    if (sameAsBefore) return false;

    openPanelKey = key;
    row.addClass('selected');
    row.after(`<tr class="detail-row"><td colspan="5"><div class="detail-panel">${html}</div></td></tr>`);
    return true;
}

function closePanel() {
    $('.detail-row').remove();
    $('.position-row').removeClass('selected');
    openPanelKey = null;
}

function panelHeader(title, subtitle) {
    return `
        <div class="detail-header">
            <div><span class="symbol">${esc(title)}</span> <span class="muted">${esc(subtitle)}</span></div>
            <button class="icon-button close-panel" aria-label="Close">✕</button>
        </div>`;
}

function tile(label, valueHtml) {
    return `<div class="tile"><span class="tile-label">${label}</span><strong>${valueHtml}</strong></div>`;
}

// ── EDIT form (UPDATE) ────────────────────────────────────────────────────
function showEdit(row, holdingId) {
    const h = holdingsById[holdingId];
    openPanel(row, holdingId + ':edit', `
        ${panelHeader('Edit ' + tickerFor(h.stockId), 'Position #' + holdingId)}
        <form class="edit-form inline-form" data-id="${holdingId}" novalidate>
            <div class="field"><label>Quantity</label>
                <input class="edit-quantity" type="number" min="0" step="0.000001" value="${Number(h.quantity)}"></div>
            <div class="field"><label>Avg price</label>
                <input class="edit-price" type="number" min="0" step="0.0001" value="${Number(h.averagePurchasePrice)}"></div>
            <button type="submit">Save changes</button>
            <button type="button" class="secondary close-panel">Cancel</button>
        </form>
    `);
}

// ── CLOSE POSITION confirm (DELETE) ───────────────────────────────────────
function showCloseConfirm(row, holdingId) {
    const h = holdingsById[holdingId];
    openPanel(row, holdingId + ':close', `
        ${panelHeader('Close ' + tickerFor(h.stockId), 'Position #' + holdingId)}
        <p>Close your <strong>${esc(tickerFor(h.stockId))}</strong> position of ${esc(Number(h.quantity))} shares?
           It will be removed from your portfolio.</p>
        <div class="ticket-buttons">
            <button class="danger confirm-close" data-id="${holdingId}">Close position</button>
            <button class="secondary close-panel">Keep it</button>
        </div>
    `);
}

// Clicking anywhere else on the page closes any open ☰ row menu.
$(document).on('click', closeRowMenus);
