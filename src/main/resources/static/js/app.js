document.addEventListener("DOMContentLoaded", function () {
    const main = document.getElementById("main");


    const loginButton = document.getElementById("loginButton");
    loginButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Login</h2>
            <form id="loginForm">
                <div>
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div>
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit">Login</button>
            </form>

            <div id="loginMessage"></div>
        `;
        document.getElementById("loginForm").addEventListener("submit", function (event) {
            event.preventDefault();
            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            $.ajax({
                type: "POST",
                url: "/api/auth/login",
                contentType: "application/json",
                data: JSON.stringify({
                    username: username,
                    password: password
                }),
                success: function (user) {
                    console.log("Logged in:", user);
                    main.innerHTML = `
                        <h2>Welcome, ${user.username}!</h2>
                        <p>You are now logged in.</p>
                    `;
                },
                error: function (xhr) {
                    console.error(xhr);
                    let message = "Login failed.";

                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        message = xhr.responseJSON.message;
                    }

                    $("#loginMessage").html(`
                        <p style="color: red;">${message}</p>
                    `);
                }
            });
        });
    });


    const registerButton = document.getElementById("registerButton");
    registerButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Register</h2>
            <form id="registerForm">
                <div>
                    <label for="registerUsername">Username</label>
                    <input type="text" id="registerUsername"
                           name="username" required>
                </div>
                <div>
                    <label for="registerPassword">Password</label>
                    <input type="password" id="registerPassword"
                           name="password" required>
                </div>
                <button type="submit">Register</button>
            </form>

            <div id="registerMessage"></div>
        `;
        document.getElementById("registerForm").addEventListener("submit", function (event) {
            event.preventDefault();
            const username = document.getElementById("registerUsername").value;
            const password = document.getElementById("registerPassword").value;
            $.ajax({
                type: "POST",
                url: "/api/auth/register",
                contentType: "application/json",
                data: JSON.stringify({
                    username: username,
                    password: password
                }),
                success: function (user) {
                    console.log("Registered:", user);

                    main.innerHTML = `
                        <h2>Registration successful</h2>
                        <p>Welcome, ${user.username}!</p>
                    `;
                },
                error: function (xhr) {
                    console.error(xhr);
                    let message = "Registration failed.";
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        message = xhr.responseJSON.message;
                    }
                    $("#registerMessage").html(`
                        <p style="color: red;">${message}</p>
                    `);
                }
            });
        });
    });


    const logoutButton = document.getElementById("logoutButton");
    logoutButton.addEventListener("click", function (event) {
        event.preventDefault();
        $.ajax({
            type: "POST",
            url: "/api/auth/logout",
            success: function () {
                main.innerHTML = `
                    <h2>Logged out</h2>
                    <p>You are now logged out.</p>
                `;
            },
            error: function (xhr) {
                console.error(xhr);

                main.innerHTML = `
                    <p style="color: red;">
                        Logout failed.
                    </p>
                `;
            }
        });
    });


    const profileButton = document.getElementById("profileButton");
    profileButton.addEventListener("click", function (event) {
        event.preventDefault();
        $.ajax({
            type: "GET",
            url: "/api/auth/me",
            success: function (user) {
                console.log("Current user:", user);
                main.innerHTML = `
                    <h2>Profile</h2>
                    <p>
                        <strong>Username:</strong>
                        ${user.username}
                    </p>
                    <p>
                        <strong>User ID:</strong>
                        ${user.userId}
                    </p>
                `;
            },
            error: function (xhr) {
                console.error(xhr);
                if (xhr.status === 401 || xhr.status === 403) {
                    main.innerHTML = `
                        <h2>Not logged in</h2>
                        <p>Please login first.</p>
                    `;
                } else {
                    main.innerHTML = `
                        <p style="color: red;">
                            Could not load profile.
                        </p>
                    `;
                }
            }
        });
    });


    const homeButton = document.getElementById("homeButton");
    homeButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Home</h2>
        `;
    });


    const portfolioButton = document.getElementById("portfolioButton");
    portfolioButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Portfolio</h2>
            <p>Loading portfolio...</p>
        `;
        $.ajax({
            type: "GET",
            url: "/api/auth/me",
            success: function (user) {
                $.ajax({
                    type: "GET",
                    url: "/api/holdings",
                    data: {
                        userId: user.userId
                    },
                    success: function (holdings) {
                        console.log("Holdings:", holdings);
                        let holdingsHtml = `
                            <h2>Portfolio</h2>
                        `;
                        if (holdings.length === 0) {
                            holdingsHtml += `
                                <p>Your portfolio is empty.</p>
                            `;
                        } else {
                            $.each(holdings, function (index, holding) {
                                holdingsHtml += `
                                    <div class="holding">
                                        <h3>${holding.stock.name}</h3>
                                        <p>
                                            Quantity:
                                            ${holding.quantity}
                                        </p>
                                        <p>
                                            Average Price:
                                            ${holding.averagePrice}
                                        </p>
                                    </div>
                                    <hr>
                                `;
                            });
                        }
                        main.innerHTML = holdingsHtml;
                    },
                    error: function (xhr) {
                        console.error("Error getting holdings:", xhr);
                        main.innerHTML = `
                            <h2>Portfolio</h2>
                            <p style="color: red;">
                                Failed to load portfolio.
                            </p>
                        `;
                    }
                });
            },
            error: function (xhr) {
                console.error("Error getting current user:", xhr);
                main.innerHTML = `
                    <h2>Portfolio</h2>
                    <p style="color: red;">
                        Please login to view your portfolio.
                    </p>
                `;
            }
        });
    });


    const watchlistButton = document.getElementById("watchlistButton");
    watchlistButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Watchlist</h2>
            <p>Loading watchlist...</p>
        `;
        $.ajax({
            type: "GET",
            url: "/api/auth/me",
            success: function (user) {
                $.ajax({
                    type: "GET",
                    url: "/api/watchlist",
                    data: {
                        userId: user.userId
                    },
                    success: function (watchlist) {
                        console.log("Watchlist:", watchlist);
                        let watchlistHtml = `
                            <h2>Watchlist</h2>
                        `;
                        if (watchlist.length === 0) {
                            watchlistHtml += `
                                <p>Your watchlist is empty.</p>
                            `;
                        } else {
                            $.each(watchlist, function (index, item) {
                                watchlistHtml += `
                                    <div class="watchlist-item">
                                        <h3>${item.stock.companyName}</h3>
                                        <p>
                                            Symbol:
                                            ${item.stock.tickerSymbol}
                                        </p>
                                    </div>
                                    <hr>
                                `;
                            });
                        }
                        main.innerHTML = watchlistHtml;
                    },
                    error: function (xhr) {
                        console.error("Error getting watchlist:", xhr);
                        main.innerHTML = `
                            <h2>Watchlist</h2>
                            <p style="color: red;">
                                Failed to load watchlist.
                            </p>
                        `;
                    }
                });
            },
            error: function (xhr) {
                console.error("Error getting current user:", xhr);
                main.innerHTML = `
                    <h2>Watchlist</h2>
                    <p style="color: red;">
                        Please login to view your watchlist.
                    </p>
                `;
            }
        });
    });


    const transactionsButton = document.getElementById("transactionsButton");
    transactionsButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Transactions</h2>
        `;
    });


    const stocksButton = document.getElementById("stocksButton");
    stocksButton.addEventListener("click", function (event) {
        event.preventDefault();
        main.innerHTML = `
            <h2>Stocks</h2>
            <p>Loading stocks...</p>
        `;
        $.ajax({
            type: "GET",
            url: "/api/v1/stocks",
            success: function (stocks) {
                console.log("Stocks received:", stocks);
                let stocksHtml = `
                    <h2>Stocks</h2>
                `;
                $.each(stocks, function (index, stock) {
                    stocksHtml += `
                        <div class="stock">
                            <h3>${stock.companyName}</h3>
                            <p>Price: ${stock.companyName}</p>
                        </div>
                        <hr>
                    `;
                });
                main.innerHTML = stocksHtml;
            },
            error: function (xhr) {
                console.error("Error getting stocks:", xhr);
                main.innerHTML = `
                    <h2>Stocks</h2>
                    <p style="color: red;">
                        Failed to load stocks.
                    </p>
                `;
            }
        });
    });



});