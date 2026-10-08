// app.js — SHARED code: helpers, navigation, login, register, profile, logout.
// Each section (portfolio, stocks, holdings, watchlist) lives in its own file
// and defines one function (showPortfolio, showStocks, ...) that this file calls.

// ── Shared helpers (used by every section file) ───────────────────────────

// Escapes text before putting it into HTML, so user data can never inject HTML/JS.
function esc(value) {
    return $('<div>').text(value === null || value === undefined ? '' : value).html();
}

// 1686.67 -> "$1,686.67";  null -> "—"
function money(value) {
    if (value === null || value === undefined) return '—';
    return '$' + Number(value).toLocaleString('en-GB', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// 23.91 -> "23.91%";  null -> "—"
function percent(value) {
    return value === null || value === undefined ? '—' : Number(value).toFixed(2) + '%';
}

// A gain shows "+" and green, a loss shows red (sign as well as colour).
function gainHtml(value, text) {
    if (value === null || value === undefined) return '—';
    const n = Number(value);
    return '<span class="' + (n < 0 ? 'loss' : 'gain') + '">' + (n > 0 ? '+' : '') + esc(text) + '</span>';
}

// Our API's errors look like {"error": "..."}; validation errors also have "fields".
function errorText(xhr, fallback) {
    const body = xhr.responseJSON;
    if (body) {
        if (body.fields) {
            return Object.entries(body.fields).map(([field, msg]) => field + ': ' + msg).join(', ');
        }
        if (body.error) return body.error;
        if (body.message) return body.message;
    }
    if (xhr.status === 401) return 'Please log in first.';
    return fallback;
}

function showMessage(text, isError) {
    $('#message').text(text || '').attr('class', 'message ' + (isError ? 'error' : 'success'));
}

function clearMessage() {
    showMessage('', false);
}

// Draws a section and highlights its sidebar link.
function showView(html, activeId) {
    $('#main').html(html);
    $('.sidebar-navigation a').removeClass('active');
    if (activeId) $('#' + activeId).addClass('active');
}

// Sends JSON to our API. Returns a jQuery promise.
function apiSend(method, url, body) {
    return $.ajax({
        type: method,
        url: url,
        contentType: 'application/json',
        data: body === undefined ? undefined : JSON.stringify(body)
    });
}

// All stocks, loaded once and reused (for tickers in tables and dropdowns).
let stockCache = null;
function loadStocks() {
    if (stockCache) return $.Deferred().resolve(stockCache).promise();
    return $.getJSON('/api/v1/stocks').then(function (stocks) {
        stockCache = stocks;
        return stocks;
    });
}

function tickerFor(stockId) {
    const s = (stockCache || []).find(st => st.stockId === stockId);
    return s ? s.tickerSymbol : 'Stock ' + stockId;
}

// Shows "Logged in as ada" in the top bar (or nothing if not logged in).
function refreshCurrentUser() {
    $.getJSON('/api/auth/me')
        .done(function (user) { $('#currentUser').text('Logged in as ' + user.username); })
        .fail(function () { $('#currentUser').text(''); });
}

// ── Login ─────────────────────────────────────────────────────────────────
function showLogin() {
    clearMessage();
    showView(`
        <section class="section">
            <h2>Login</h2>
            <form id="loginForm" novalidate>
                <div class="field"><label for="username">Username</label><input id="username"></div>
                <div class="field"><label for="password">Password</label><input id="password" type="password"></div>
                <button type="submit">Login</button>
            </form>
        </section>
    `);

    $('#loginForm').on('submit', function (event) {
        event.preventDefault();
        const username = $('#username').val().trim();
        const password = $('#password').val();
        if (!username || !password) return showMessage('Enter your username and password.', true);

        apiSend('POST', '/api/auth/login', { username: username, password: password })
            .done(function (user) {
                showMessage('Welcome, ' + user.username + '!', false);
                refreshCurrentUser();
                showPortfolio();   // after logging in, go straight to the portfolio
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Login failed.'), true); });
    });
}

// ── Register ──────────────────────────────────────────────────────────────
function showRegister() {
    clearMessage();
    showView(`
        <section class="section">
            <h2>Register</h2>
            <form id="registerForm" novalidate>
                <div class="field"><label for="registerUsername">Username</label><input id="registerUsername"></div>
                <div class="field"><label for="registerEmail">Email</label><input id="registerEmail" type="email"></div>
                <div class="field"><label for="registerPassword">Password</label><input id="registerPassword" type="password"></div>
                <div class="field"><label for="registerConfirm">Confirm password</label><input id="registerConfirm" type="password"></div>
                <button type="submit">Register</button>
            </form>
            <p class="muted">3–30 letters, digits or _ for the username. Password: 8+ characters with a letter and a digit.</p>
        </section>
    `);

    $('#registerForm').on('submit', function (event) {
        event.preventDefault();
        const username = $('#registerUsername').val().trim();
        const email = $('#registerEmail').val().trim();
        const password = $('#registerPassword').val();
        const confirm = $('#registerConfirm').val();

        // Front-end checks that mirror RegisterRequestDto (the server checks again).
        if (!username || !email || !password) return showMessage('Please fill in every field.', true);
        if (username.length < 3 || username.length > 30 || !/^[A-Za-z0-9_]+$/.test(username)) {
            return showMessage('Username must be 3–30 letters, digits or underscores.', true);
        }
        if (!email.includes('@')) return showMessage('Enter a valid email address.', true);
        if (password.length < 8 || !/[A-Za-z]/.test(password) || !/\d/.test(password)) {
            return showMessage('Password must be at least 8 characters with a letter and a digit.', true);
        }
        if (password !== confirm) return showMessage('The passwords do not match.', true);

        apiSend('POST', '/api/auth/register', { username: username, email: email, password: password })
            .done(function () {
                showLogin();
                showMessage('Account created. Please log in.', false);
            })
            .fail(function (xhr) { showMessage(errorText(xhr, 'Registration failed.'), true); });
    });
}

// ── Profile ───────────────────────────────────────────────────────────────
function showProfile() {
    clearMessage();
    $.getJSON('/api/auth/me')
        .done(function (user) {
            showView(`
                <section class="section">
                    <h2>Profile</h2>
                    <p><strong>Username:</strong> ${esc(user.username)}</p>
                    <p><strong>Email:</strong> ${esc(user.email)}</p>
                    <p><strong>User ID:</strong> ${esc(user.userId)}</p>
                </section>
            `);
        })
        .fail(function (xhr) { showView(''); showMessage(errorText(xhr, 'Could not load profile.'), true); });
}

// ── Logout ────────────────────────────────────────────────────────────────
function logout() {
    apiSend('POST', '/api/auth/logout')
        .always(function () {
            stockCache = null;
            refreshCurrentUser();
            showLogin();
            showMessage('You are now logged out.', false);
        });
}

// ── Navigation: which function each link runs ─────────────────────────────
$(function () {
    function link(id, handler) {
        $('#' + id).on('click', function (event) {
            event.preventDefault();
            handler();
        });
    }

    link('homeButton', function () {
        clearMessage();
        showView(`
            <section class="section">
                <h2>Welcome to Portfolio Tracker</h2>
                <p>Track your stocks with live prices from Twelve Data.</p>
                <p class="muted">Use the menu on the left: Portfolio for your totals, Holdings to add, edit or delete stocks, and Stocks for live prices.</p>
            </section>
        `);
    });
    link('loginButton', showLogin);
    link('registerButton', showRegister);
    link('profileButton', showProfile);
    link('logoutButton', logout);
    link('portfolioButton', showPortfolio);
    link('holdingsButton', showHoldings);
    link('stocksButton', showStocks);
    link('watchlistButton', showWatchlist);
    link('transactionsButton', function () {
        clearMessage();
        showView('<section class="section"><h2>Transactions</h2><p class="muted">Coming soon.</p></section>', 'transactionsButton');
    });

    // On first load: logged in → portfolio; not logged in → login form.
    $.getJSON('/api/auth/me')
        .done(function (user) {
            $('#currentUser').text('Logged in as ' + user.username);
            showPortfolio();
        })
        .fail(showLogin);
});
