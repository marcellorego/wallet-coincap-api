package com.example.demo.service;

import com.example.demo.config.properties.CoinCapProperties;
import com.example.demo.service.client.CoinCapAssets;
import com.example.demo.test.SpringProfileRunnerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CoinCapServiceTest extends SpringProfileRunnerTest {

    @Autowired
    private CoinCapService coinCapService;

    @Autowired
    private CoinCapProperties coinCapProperties;

    @Test
    void testGroupAssetSymbols() {

        Set<String> requestedSymbols =
                Set.of("BTC", "ETH", "XRP", "LTC", "BCH", "EOS", "BNB", "USDT", "XLM", "ADA");

        var assetsPageSize = coinCapProperties.getAssetsPageSize();
        var expectedGroupSize = (int) Math.ceil((float)requestedSymbols.size() / assetsPageSize);

        List<Set<String>> groupedSymbols = coinCapService.groupAssetSymbols(requestedSymbols);

        assertThat(groupedSymbols).isNotEmpty().hasSize(expectedGroupSize);
        assertThat(groupedSymbols.get(0)).isNotEmpty().hasSize(coinCapProperties.getAssetsPageSize());
        assertThat(groupedSymbols.get(1)).isNotEmpty().hasSize(coinCapProperties.getAssetsPageSize());
    }

    @Test
    void testGetCoinCapAssetsAsync() {

        Set<String> requestedSymbols =
                Set.of("BTC", "ETH", "XRP", "LTC", "BCH", "EOS", "BNB", "USDT", "XLM", "ADA");

        Optional<CoinCapAssets> coinCapAssetsOptional =
                coinCapService.getCoinCapAssetsAsync(requestedSymbols);

        assertThat(coinCapAssetsOptional).isPresent();

        var coinCapAssets = coinCapAssetsOptional.get();
        assertThat(coinCapAssets.getData()).isNotEmpty().hasSize(requestedSymbols.size());

        assertThat(coinCapAssets.getData())
                .extracting("symbol")
                .contains("BTC", "ETH", "XRP", "LTC", "BCH", "EOS", "BNB", "USDT", "XLM", "ADA");
    }
}
