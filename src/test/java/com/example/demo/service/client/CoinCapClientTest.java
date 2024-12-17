package com.example.demo.service.client;

import com.example.demo.test.SpringProfileRunnerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class CoinCapClientTest extends SpringProfileRunnerTest {

    @Autowired
    private CoinCapClient coinCapClient;

    @Test
    void testGetAssets() {

        // Act
        CoinCapAssets result = coinCapClient.getAssets(1, 0);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotEmpty().hasSize(1);
    }

    @Test
    void testGetAsset() {

        // Act
        CoinCapAsset result = coinCapClient.getAsset("bitcoin");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
    }

    @Test
    void getAssetBySearchId() {

        // Act
        Optional<CoinCapAssets> resultOptional = coinCapClient.getAssetBySearchId("BTC");

        // Assert
        assertThat(resultOptional)
                .isPresent()
                .get()
                .satisfies(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getData()).isNotEmpty().hasSize(1);
                });
    }

    @Test
    void testGetAssetHistory() {

        Instant start = ZonedDateTime.parse("2024-12-13T00:00:00Z").toInstant();
        Instant end = ZonedDateTime.parse("2024-12-13T00:00:01Z").toInstant();

        // Act
        CoinCapHistoryAssets result = coinCapClient.getAssetHistoryByPeriod("bitcoin",
                start.toEpochMilli(), end.toEpochMilli(), "m1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotEmpty().hasSize(1);
        assertThat(result.getData().get(0).getPriceUsd()).isNotNull();
        assertThat(result.getData().get(0).getDate()).isEqualTo("2024-12-13T00:00:00Z");
        assertThat(result.getTimestamp()).isPositive();
    }
}
