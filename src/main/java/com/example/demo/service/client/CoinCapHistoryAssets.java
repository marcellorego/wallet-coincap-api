package com.example.demo.service.client;

import lombok.Data;

import java.util.List;

/**
 * CoinCap history assets.
 */

@Data
public class CoinCapHistoryAssets {
    private List<AssetHistoryData> data;
    private long timestamp;
}
