// home.js — the landing page, with live market widgets from TradingView.
// TradingView widgets are free, need no API key, and run in the browser:
// our backend is not involved at all.

function showHome() {
    clearMessage();
    showView(`
        <section class="section hero">
            <h2>Welcome to Portfolio Tracker</h2>
            <p>Track your investments with live prices, and see your profit or loss at a glance.</p>
        </section>

        <section class="section">
            <h2>Markets right now</h2>
            <div class="tradingview-widget-container" id="tickerTape">
                <div class="tradingview-widget-container__widget"></div>
            </div>
        </section>

        <section class="section">
            <h2>Market overview</h2>
            <div class="tradingview-widget-container" id="marketOverview">
                <div class="tradingview-widget-container__widget"></div>
            </div>
            <p class="muted">Market data by <a href="https://www.tradingview.com/" target="_blank" rel="noopener">TradingView</a></p>
        </section>
    `, 'homeButton');

    addTradingViewWidget('tickerTape', 'ticker-tape', {
        symbols: [
            { proName: 'FOREXCOM:SPXUSD', title: 'S&P 500' },
            { proName: 'FOREXCOM:NSXUSD', title: 'Nasdaq 100' },
            { proName: 'NASDAQ:AAPL', title: 'Apple' },
            { proName: 'NASDAQ:MSFT', title: 'Microsoft' },
            { proName: 'NASDAQ:NVDA', title: 'NVIDIA' },
            { proName: 'NASDAQ:TSLA', title: 'Tesla' },
            { proName: 'NASDAQ:AMZN', title: 'Amazon' }
        ],
        showSymbolLogo: true,
        colorTheme: 'dark',
        isTransparent: true,
        displayMode: 'adaptive',
        locale: 'en'
    });

    addTradingViewWidget('marketOverview', 'market-overview', {
        colorTheme: 'dark',
        dateRange: '12M',
        showChart: true,
        locale: 'en',
        width: '100%',
        height: 450,
        isTransparent: true,
        showSymbolLogo: true,
        tabs: [
            {
                title: 'Tech stocks',
                symbols: [
                    { s: 'NASDAQ:AAPL', d: 'Apple' },
                    { s: 'NASDAQ:MSFT', d: 'Microsoft' },
                    { s: 'NASDAQ:NVDA', d: 'NVIDIA' },
                    { s: 'NASDAQ:GOOGL', d: 'Alphabet' },
                    { s: 'NASDAQ:AMZN', d: 'Amazon' },
                    { s: 'NASDAQ:TSLA', d: 'Tesla' }
                ]
            }
        ]
    });
}

// Adds one TradingView widget into the container with the given id.
// TradingView's script reads its settings from the text inside its own <script> tag,
// so we build that tag by hand (jQuery's .html() would not run it properly).
function addTradingViewWidget(containerId, widgetName, settings) {
    const script = document.createElement('script');
    script.src = 'https://s3.tradingview.com/external-embedding/embed-widget-' + widgetName + '.js';
    script.async = true;
    script.innerHTML = JSON.stringify(settings);
    document.getElementById(containerId).appendChild(script);
}