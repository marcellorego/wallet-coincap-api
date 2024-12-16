package com.example.demo.service.client;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Data class for asset data.
 * Represents a single asset from the CoinCap API with the following structure:
 * {
 * "id": "bitcoin",
 * "rank": "1",
 * "symbol": "BTC",
 * "name": "Bitcoin",
 * "supply": "19796178.0000000000000000",
 * "maxSupply": "21000000.0000000000000000",
 * "marketCapUsd": "2010691895247.7796756587871904",
 * "volumeUsd24Hr": "8182926148.9130740721973562",
 * "priceUsd": "101569.7017498923113168",
 * "changePercent24Hr": "-0.7520724270817843",
 * "vwap24Hr": "101586.1765511200635114",
 * "explorer": "https://blockchain.info/"
 * }
 */

@Data
public class AssetData {

    private String id;
    private String symbol;
    private String name;
    private BigDecimal priceUsd;
    private String rank;
    private BigDecimal supply;
    private BigDecimal maxSupply;
    private BigDecimal marketCapUsd;
    private BigDecimal volumeUsd24Hr;
    private BigDecimal changePercent24Hr;
    private BigDecimal vwap24Hr;
    private String explorer;
}
