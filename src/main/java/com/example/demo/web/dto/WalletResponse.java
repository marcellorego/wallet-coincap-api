package com.example.demo.web.dto;

import com.example.demo.service.client.AssetData;
import lombok.Data;

import java.util.Set;

/**
 * Wallet response DTO.
 */

@Data
public final class WalletResponse {
    private String id;
    private Set<AssetData> assetData;
}
