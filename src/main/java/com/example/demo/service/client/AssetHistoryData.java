package com.example.demo.service.client;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * CoinCap asset history.
 * Represents an assets history data from the CoinCap API with the following structure:
 * {
 * "priceUsd": "100499.6993084202031975",
 * "time": 1734048000000,
 * "date": "2024-12-13T00:00:00.000Z"
 * }
 */

@Data
public class AssetHistoryData {

    private BigDecimal priceUsd;
    private ZonedDateTime date;
    private long time;
}
