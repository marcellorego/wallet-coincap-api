package com.example.demo.service;

import com.example.demo.config.properties.CoinCapProperties;
import com.example.demo.service.client.AssetData;
import com.example.demo.service.client.CoinCapAssets;
import com.example.demo.service.client.CoinCapClient;
import com.example.demo.service.client.CoinCapException;
import com.example.demo.util.ElapsedTimeWatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Service implementation for CoinCap API calls to rest client.
 */

@Service
@Slf4j
public class CoinCapService {

    private static final int ASSETS_PER_REQUEST = 3;
    private static final int REQUEST_TIMEOUT = 10;
    private static final DateTimeFormatter INSTANT_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    @Getter
    private final Clock defaultClock;
    private final CoinCapProperties coinCapProperties;
    private final CoinCapClient coinCapClient;
    private final AssetService assetService;

    public CoinCapService(final Clock defaultClock,
                          final CoinCapProperties coinCapProperties,
                          final CoinCapClient coinCapClient,
                          final AssetService assetService) {
        this.defaultClock = defaultClock;
        this.coinCapProperties = coinCapProperties;
        this.coinCapClient = coinCapClient;
        this.assetService = assetService;
    }

    /**
     * Formats the Instant value to a string.
     * @param value the Instant value
     * @return the formatted string
     */
    private String formatInstant(final Instant value) {
        final LocalDateTime dateTime = LocalDateTime.ofInstant(value, ZoneId.systemDefault());
        return dateTime.format(INSTANT_FORMATTER);
    }

    /**
     * Groups the requested asset symbols into groups of ASSETS_PER_REQUEST.
     *
     * @param requestedSymbols the requested symbols
     * @return the grouped asset symbols
     */
    protected List<List<String>> groupAssetSymbols(final List<String> requestedSymbols) {

        // Group the requested asset symbols into groups of ASSETS_PER_REQUEST
        final List<List<String>> groupedAssetSymbols = new ArrayList<>();

        List<String> currentGroup = new ArrayList<>();
        for (String symbol : requestedSymbols) {
            currentGroup.add(symbol);
            if (currentGroup.size() == ASSETS_PER_REQUEST) {
                groupedAssetSymbols.add(currentGroup);
                currentGroup = new ArrayList<>();
            }
        }
        if (!currentGroup.isEmpty()) {
            groupedAssetSymbols.add(currentGroup);
        }

        return groupedAssetSymbols;
    }

    /**
     * Fetches CoinCap assets for the requested symbols.
     *
     * @param requestedSymbols the requested symbols
     * @return the CoinCapAssets assets
     */
    public CoinCapAssets getCoinCapAssetsAsync(final List<String> requestedSymbols) {

        log.debug("Fetching CoinCap assets for {}", requestedSymbols);

        final CoinCapAssets result = new CoinCapAssets();
        result.setData(new ArrayList<>());

        final List<List<String>> groupedAssetSymbols = groupAssetSymbols(requestedSymbols);

        log.debug("getCoinCapAssetsAsync :: Start fetching CoinCap for {} groups", groupedAssetSymbols.size());

        log.debug("Now is {}", formatInstant(defaultClock.instant()));

        final ElapsedTimeWatcher watcher = ElapsedTimeWatcher.start();

        // Create an executor service with a fixed thread pool of ASSETS_PER_REQUEST threads
        final ExecutorService executorService = Executors
                .newFixedThreadPool(ASSETS_PER_REQUEST);

        for (List<String> symbols : groupedAssetSymbols) {

            // Create an array of CompletableFuture objects
            // Fetch the asset data for each asset ID
            final List<CompletableFuture<AssetData>> futures = symbols
                    .stream()
                    .map(symbol -> CompletableFuture.supplyAsync(() -> {
                        log.debug("Submitted request {} at {}", symbol, formatInstant(defaultClock.instant()));
                        return getAssetDataBySymbol(symbol);
                    }, executorService))
                    .toList();
            // Wait for all downloads to complete and print the downloaded contents
            final List<AssetData> completed = getAllCompleted(futures);
            // Add the completed assets to the result
            result.getData().addAll(completed);
        }

        executorService.shutdown();

        final String end = watcher.elapsedTimeInSeconds();
        log.info("getCoinCapAssetsAsync :: Finished fetching CoinCap at: {}", defaultClock.instant());
        log.info("getCoinCapAssetsAsync :: Elapsed time: {} seconds", end);

        return result;
    }

    /**
     * Waits for all futures to complete and returns the completed futures.
     * @param futuresList future with requested symbols
     * @return List of AssetData
     */
    private List<AssetData> getAllCompleted(final List<CompletableFuture<AssetData>> futuresList) {
        final CompletableFuture<Void> allFuturesResult = CompletableFuture
                .allOf(futuresList.toArray(new CompletableFuture[0]));
        try {
            allFuturesResult.get(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("Error waiting for futures to complete", e);
        }
        return futuresList
                .stream()
                .filter(future -> future.isDone() && !future.isCompletedExceptionally()) // keep only the ones completed
                .map(CompletableFuture::join) // get the value from the completed future
                .toList(); // collect as a list
    }

    /**
     * Fetches CoinCap assets for the requested symbols.
     *
     * @param symbol the requested symbol
     * @return AssetData
     */
    protected AssetData getAssetDataBySymbol(final String symbol) {
        return coinCapClient.getAssetBySymbol(symbol)
                .map(CoinCapAssets::getData)
                .flatMap(assets -> assets.stream().findFirst())
                .orElse(null);
    }

    /**
     * Fetches all assets from CoinCap API.
     * @param assetPageSize
     * @param coinCapAssetsConsumer
     * @return
     * @throws CoinCapException
     */
    private CoinCapAssets fetchAllAssets(int assetPageSize, Consumer<List<AssetData>> coinCapAssetsConsumer) throws CoinCapException {

        final CoinCapAssets result = new CoinCapAssets();
        result.setData(new ArrayList<>());

        int offset = 0;
        CoinCapAssets assets = coinCapClient.getAssets(assetPageSize, offset);
        while (Objects.nonNull(assets) && Objects.nonNull(assets.getData()) && !assets.getData().isEmpty()) {

            final List<AssetData> filteredData = assets
                    .getData()
                    .stream()
                    // Remove assets without an ID
                    .filter(a -> StringUtils.hasLength(a.getId()))
                    // Remove assets without a symbol
                    .filter(a -> StringUtils.hasLength(a.getSymbol()))
                    // Remove assets without price
                    .filter(a -> Objects.nonNull(a.getPriceUsd()))
                    .toList();

            if (Objects.nonNull(coinCapAssetsConsumer)) {
                coinCapAssetsConsumer.accept(filteredData);
            }
            result.getData().addAll(filteredData);
            offset += assetPageSize;
            assets = coinCapClient.getAssets(assetPageSize, offset);
        }

        return result;
    }

    public void updateAssets() {
        try {
            final CoinCapAssets assets = fetchAllAssets(coinCapProperties.getAssetsPageSize(), assetData -> {
                log.info("updateAssets :: Fetched {} assets", assetData.size());
                assetData.forEach(a -> {
                    try {
                        assetService.saveAsset(a);
                    } catch (Exception e) {
                        log.error("updateAssets :: Error saving asset {}", a, e);
                    }
                });
            });
            log.info("updateAssets :: Fetched {} assets", assets.getData().size());
        } catch (CoinCapException e) {
            log.error("updateAssets :: Error updating assets", e);
        }
    }

}
