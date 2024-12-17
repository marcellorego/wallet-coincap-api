package com.example.demo.service.client;

import lombok.Data;

import java.util.List;

/**
 * CoinCap history assets.
 */

@Data
public final class CoinCapHistoryAssets {
    private List<AssetHistoryData> data;
    private long timestamp;
}
