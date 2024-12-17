package com.example.demo.service;

import com.example.demo.config.properties.CoinCapProperties;
import com.example.demo.entity.Asset;
import com.example.demo.service.client.*;
import com.example.demo.util.ElapsedTimeWatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service implementation for CoinCap API calls to rest client.
 */

@Service
@Slf4j
public class CoinCapService {

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
     *
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
    protected List<Set<String>> groupAssetSymbols(final Set<String> requestedSymbols) {

        // Group the requested asset symbols into groups of ASSETS_PER_REQUEST
        final List<Set<String>> groupedAssetSymbols = new ArrayList<>();

        Set<String> currentGroup = new LinkedHashSet<>();
        for (String symbol : requestedSymbols) {
            currentGroup.add(symbol);
            if (currentGroup.size() == coinCapProperties.getAssetsPageSize()) {
                groupedAssetSymbols.add(currentGroup);
                currentGroup = new LinkedHashSet<>();
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
    public Optional<CoinCapAssets> getCoinCapAssetsAsync(final Set<String> requestedSymbols) {

        log.debug("Fetching CoinCap assets for {}", requestedSymbols);

        final List<Set<String>> groupedAssetSymbols = groupAssetSymbols(requestedSymbols);

        log.debug("getCoinCapAssetsAsync :: Start fetching CoinCap for {} groups", groupedAssetSymbols.size());

        log.debug("Now is {}", formatInstant(defaultClock.instant()));

        final ElapsedTimeWatcher watcher = ElapsedTimeWatcher.start();

        // Create an executor service with a fixed thread pool of ASSETS_PER_REQUEST threads
        final ExecutorService executorService = Executors
                .newFixedThreadPool(coinCapProperties.getAssetsPageSize());

        Optional<CoinCapAssets> result = Optional.empty();
        try {
            result = Optional.of(fetchGroupedAssets(groupedAssetSymbols, executorService));
        } finally {
            executorService.shutdown();
        }

        final String end = watcher.elapsedTimeInSeconds();
        log.info("getCoinCapAssetsAsync :: Finished fetching CoinCap at: {}", defaultClock.instant());
        log.info("getCoinCapAssetsAsync :: Elapsed time: {} seconds", end);

        return result;
    }

    protected CoinCapAssets fetchGroupedAssets(final List<Set<String>> groupedAssetSearchIds,
                                               final ExecutorService executorService) {

        final CoinCapAssets result = new CoinCapAssets();
        result.setData(new ArrayList<>());
        // Set the timestamp to the current time
        result.setTimestamp(defaultClock.instant().toEpochMilli());

        for (Set<String> searchIds : groupedAssetSearchIds) {

            // Create an array of CompletableFuture objects
            // Fetch the asset data for each asset ID
            final List<CompletableFuture<AssetData>> futures = searchIds
                    .stream()
                    .map(searchId -> CompletableFuture.supplyAsync(() -> {
                        log.debug("Submitted request {} at {}", searchId, formatInstant(defaultClock.instant()));
                        return getAssetBySearchId(searchId);
                    }, executorService))
                    .toList();
            // Wait for all downloads to complete and print the downloaded contents
            final List<AssetData> completed = getAllCompleted(futures);
            // Add the completed assets to the result
            result.getData().addAll(completed);
        }

        return result;
    }

    /**
     * Waits for all futures to complete and returns the completed futures.
     *
     * @param futuresList future with requested symbols
     * @return List of AssetData
     */
    private List<AssetData> getAllCompleted(final List<CompletableFuture<AssetData>> futuresList) {
        final CompletableFuture<Void> allFuturesResult = CompletableFuture
                .allOf(futuresList.toArray(new CompletableFuture[0]));
        try {
            allFuturesResult.get(coinCapProperties.getAssetsRequestTimeout(), TimeUnit.SECONDS);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("Error waiting for futures to complete", e);
        }
        return futuresList
                .stream()
                .filter(future -> future.isDone() && !future.isCompletedExceptionally()) // keep only the ones completed
                .map(CompletableFuture::join) // get the value from the completed future
                .filter(Objects::nonNull) // remove null values
                .toList(); // collect as a list
    }

    /**
     * Fetches CoinCap assets for the requested searchId.
     *
     * @param searchId the requested searchId
     * @return AssetData
     */
    protected AssetData getAssetBySearchId(final String searchId) {
        return coinCapClient.getAssetBySearchId(searchId)
                .map(CoinCapAssets::getData)
                .flatMap(assets -> assets.stream().findFirst())
                .orElse(null);
    }

    /**
     * Fetches CoinCap assets for the requested assetIds.
     *
     * @param assetId the requested assetId
     * @return AssetData
     */
    protected BigDecimal getAssetHistoryPriceAt(final String assetId, final Instant performanceAt) {
        final Instant startDate = performanceAt.truncatedTo(ChronoUnit.SECONDS);
        final Instant endDate = performanceAt.truncatedTo(ChronoUnit.SECONDS)
                .plus(1, ChronoUnit.MINUTES);

        CoinCapHistoryAssets historyAsset = null;
        try {
            historyAsset = coinCapClient.getAssetHistoryByPeriod(assetId,
                    startDate.toEpochMilli(),
                    endDate.toEpochMilli(),
                    "m1");
        } catch (CoinCapException e) {
            log.error("Error fetching asset history price at {} for asset {} with code {}",
                    performanceAt, assetId, e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Error fetching asset history price at {} for asset {}", performanceAt, assetId, e);
        }

        return Optional.ofNullable(historyAsset)
                .map(CoinCapHistoryAssets::getData)
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .map(AssetHistoryData::getPriceUsd)
                .orElse(null);
    }

    public void updateAssets() {
        try {
            final List<Asset> existingAssets = assetService.findAllAssets();

            final Set<String> existingAssetIds = existingAssets
                    .stream()
                    .map(Asset::getId)
                    .collect(Collectors.toSet());

            final List<AssetData> assetDataList = getCoinCapAssetsAsync(existingAssetIds)
                    .map(CoinCapAssets::getData)
                    .orElse(Collections.emptyList());

            assetDataList.forEach(assetData -> {
                try {
                    assetService.saveAsset(assetData);
                } catch (Exception e) {
                    log.error("updateAssets :: Error saving asset {}", assetData, e);
                }
            });

            log.info("updateAssets :: Fetched {} assets", assetDataList.size());
        } catch (CoinCapException e) {
            log.error("updateAssets :: Error updating assets", e);
        }
    }

}
