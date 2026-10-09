-- data.sql : demo data for stockportfoliodb
-- Safe to re-run: it empties every table first, then inserts fresh rows.
-- Every demo user's password is: Password123!

USE stockportfoliodb;

-- 1. Empty the tables (children first) and reset the ids back to 1
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE transaction_history;
TRUNCATE TABLE watchlist_item;
TRUNCATE TABLE holding;
TRUNCATE TABLE stock;
TRUNCATE TABLE user_account;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. Users (passwordHash is a real BCrypt hash of Password123!)
INSERT INTO user_account (userId, username, email, passwordHash) VALUES
(1, 'ada',   'ada@example.com',   '$2a$10$P2VRBhKxutpirZrgRIfYtOigds5Z78DmvqKLGwKau0u/Mgjdo8xwC'),
(2, 'alan',  'alan@example.com',  '$2a$10$P2VRBhKxutpirZrgRIfYtOigds5Z78DmvqKLGwKau0u/Mgjdo8xwC'),
(3, 'grace', 'grace@example.com', '$2a$10$P2VRBhKxutpirZrgRIfYtOigds5Z78DmvqKLGwKau0u/Mgjdo8xwC');

-- 3. Stocks (the shared list of companies)
INSERT INTO stock (stockId, tickerSymbol, companyName) VALUES
(1, 'AAPL',  'Apple Inc'),
(2, 'MSFT',  'Microsoft Corporation'),
(3, 'GOOGL', 'Alphabet Inc'),
(4, 'AMZN',  'Amazon.com Inc'),
(5, 'TSLA',  'Tesla Inc'),
(6, 'NVDA',  'NVIDIA Corporation');

-- 4. Holdings: what each user owns right now (one row per user and stock)
--    Ada's AAPL row is the worked example: 11 shares at an average of 153.3333
INSERT INTO holding (holdingId, userId, stockId, quantity, averagePurchasePrice) VALUES
(1, 1, 1, 11.000000, 153.3333),   -- Ada:   11 AAPL
(2, 1, 2,  5.000000, 420.0000),   -- Ada:    5 MSFT
(3, 2, 3, 20.000000, 120.0000),   -- Alan:  20 GOOGL
(4, 3, 1, 15.000000, 170.0000),   -- Grace: 15 AAPL
(5, 3, 4,  4.000000, 140.0000);   -- Grace:  4 AMZN

-- 5. Transaction history: the diary of changes that led to those holdings
INSERT INTO transaction_history (transactionId, userId, stockId, transactionType, quantity, price, transactionDate) VALUES
(1, 1, 1, 'BUY',  10.000000, 150.0000, '2025-01-15 10:00:00'),
(2, 1, 1, 'BUY',   5.000000, 153.3333, '2025-03-03 10:00:00'),
(3, 1, 1, 'SELL',  4.000000, 153.3333, '2025-06-10 10:00:00'),
(4, 1, 2, 'BUY',   5.000000, 420.0000, '2025-03-10 10:00:00'),
(5, 2, 3, 'BUY',  20.000000, 120.0000, '2024-11-02 10:00:00'),
(6, 3, 1, 'BUY',  15.000000, 170.0000, '2025-08-05 10:00:00'),
(7, 3, 4, 'BUY',   4.000000, 140.0000, '2024-09-12 10:00:00');

-- 6. Watchlist (optional feature): stocks followed but not owned
INSERT INTO watchlist_item (watchlistItemId, userId, stockId) VALUES
(1, 1, 5),   -- Ada watches TSLA
(2, 1, 6),   -- Ada watches NVDA
(3, 2, 1);   -- Alan watches AAPL

-- 7. Quick checks: run these one at a time after the inserts
-- All of Ada's holdings, with the ticker and company name joined in:
-- SELECT h.holdingId, s.tickerSymbol, s.companyName, h.quantity, h.averagePurchasePrice
-- FROM holding h JOIN stock s ON h.stockId = s.stockId
-- WHERE h.userId = 1;

-- One holding, only if it belongs to the user (try userId = 2 to see "own data only"):
-- SELECT * FROM holding WHERE holdingId = 1 AND userId = 1;
