CREATE TABLE IF NOT EXISTS wallet (
    id VARCHAR(20) PRIMARY KEY,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_usd DECIMAL(12, 2) NOT NULL, -- 9999999999.00 total financial value in USD
    best_asset VARCHAR(5) NOT NULL,
    best_performance FLOAT NOT NULL, -- 9999.000 % performance
    worst_asset VARCHAR(5) NOT NULL,
    worst_performance FLOAT NOT NULL, -- 9999.000 % performance
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS asset (
    id VARCHAR(40) PRIMARY KEY,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    symbol VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_usd DECIMAL(30, 20) NOT NULL, -- 9999999999.00007248741714484778
    PRIMARY KEY (id)
);