package com.example.demo.service;

import com.example.demo.mapper.PerformanceMapper;
import com.example.demo.repository.PerformanceRepository;
import com.example.demo.service.client.AssetData;
import com.example.demo.service.client.CoinCapAssets;
import com.example.demo.web.dto.RequestedAsset;
import com.example.demo.web.dto.WalletPerformanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceServiceMockTest {


    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceMapper performanceMapper;

    @InjectMocks
    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCalculateWalletValueWithNullValues() {
        // Arrange
        // Act
        WalletPerformanceResponse response = performanceService.calculateWalletValue(null, null);
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotal()).isZero();
        assertThat(response.getBestPerformance()).isZero();
        assertThat(response.getBestAsset()).isEmpty();
        assertThat(response.getWorstPerformance()).isZero();
        assertThat(response.getWorstAsset()).isEmpty();
    }

    @Test
    void testCalculateWalletValueWithEmptyValues() {
        // Arrange
        // Act
        WalletPerformanceResponse response = performanceService
                .calculateWalletValue(Collections.emptySet(),
                        new CoinCapAssets());
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotal()).isZero();
        assertThat(response.getBestPerformance()).isZero();
        assertThat(response.getBestAsset()).isEmpty();
        assertThat(response.getWorstPerformance()).isZero();
        assertThat(response.getWorstAsset()).isEmpty();
    }

    @Test
    void testCalculateWalletValueWithValues() {
        // Arrange
        final Set<RequestedAsset> requestedAssetList = Set.of(
                RequestedAsset.builder()
                        .symbol("BTC")
                        .quantity(BigDecimal.valueOf(1))
                        .price(BigDecimal.valueOf(10000.00))
                        .build(),
                RequestedAsset.builder()
                        .symbol("ETH")
                        .quantity(BigDecimal.valueOf(2))
                        .price(BigDecimal.valueOf(500.00))
                        .build()
        );

        final AssetData asset1 = new AssetData();
        asset1.setSymbol("BTC");
        asset1.setPriceUsd(BigDecimal.valueOf(20000.00));

        final AssetData asset2 = new AssetData();
        asset2.setSymbol("ETH");
        asset2.setPriceUsd(BigDecimal.valueOf(100.00));

        final CoinCapAssets coinCapAssets = new CoinCapAssets();
        coinCapAssets.setData(
                List.of(
                        asset1,
                        asset2
                )
        );

        // Act
        WalletPerformanceResponse response = performanceService
                .calculateWalletValue(requestedAssetList, coinCapAssets);
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotal()).isEqualTo(BigDecimal.valueOf(20200.00d));
        assertThat(response.getBestPerformancePercentage()).isEqualTo(100.00f);
        assertThat(response.getBestAsset()).isEqualTo("BTC");
        assertThat(response.getWorstPerformancePercentage()).isEqualTo(-80.00f);
        assertThat(response.getWorstAsset()).isEqualTo("ETH");
    }

}
