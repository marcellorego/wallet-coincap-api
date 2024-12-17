package com.example.demo.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class CoinCapClientMockTest {

    @Mock
    private CoinCapClient coinCapClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAssets() {

        // Arrange
        CoinCapAssets mockAssets = new CoinCapAssets();
        mockAssets.setData(Collections.singletonList(new AssetData()));

        when(coinCapClient.get(any(), eq(CoinCapAssets.class)))
                .thenReturn(mockAssets);
        when(coinCapClient.getAssets(1, 0))
                .thenCallRealMethod();

        // Act
        CoinCapAssets result = coinCapClient.getAssets(1, 0);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotEmpty().hasSize(1);
    }


    @Test
    void testGetAsset() {

        // Arrange
        AssetData data = new AssetData();
        data.setId("1");

        CoinCapAsset mockAsset = new CoinCapAsset();
        mockAsset.setData(data);
        mockAsset.setTimestamp(1734225918610L);

        when(coinCapClient.get(any(), eq(CoinCapAsset.class)))
                .thenReturn(mockAsset);
        when(coinCapClient.getAsset(anyString()))
                .thenCallRealMethod();

        // Act
        CoinCapAsset result = coinCapClient.getAsset("1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
    }
}
