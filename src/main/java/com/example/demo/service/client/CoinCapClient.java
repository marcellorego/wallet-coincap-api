package com.example.demo.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * CoinCap client to fetch data from CoinCap API
 */

@Component
@Slf4j
public class CoinCapClient {

    private final RestClient restClient;

    public CoinCapClient(@Qualifier("coinCapRestClient") final RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Fetches data from CoinCap API
     *
     * @param template the template to fetch data
     * @param clazz    the class to map the response
     * @param <T>      the type of the response
     * @return the response
     * @throws CoinCapException if there is an error fetching data
     */
    public <T> T get(final String template, final Class<T> clazz) throws CoinCapException {
        return restClient.get()
                .uri(template)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new CoinCapException("Server error while requesting url %s".formatted(template),
                                    response.getStatusCode(),
                                    null);
                        })
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new CoinCapException("Client error while requesting to url %s".formatted(template),
                                    response.getStatusCode(),
                                    null);
                        })
                .body(clazz);
    }

    /**
     * Fetches asset by search identifier
     *
     * @param searchId the search identifier of the asset
     * @return the asset
     * @throws CoinCapException if there is an error fetching data
     */
    public Optional<CoinCapAssets> getAssetBySearchId(final String searchId) throws CoinCapException {

        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/assets")
                .queryParam("search", searchId)
                .queryParam("offset", 0)
                .queryParam("limit", 1)
                .build();

        try {
            final CoinCapAssets result = get(uriComponents.toUriString(), CoinCapAssets.class);
            log.debug("Fetched assets from offset {} limit {}: {}", 1, 1, result);
            return Optional.of(result);
        } catch (CoinCapException e) {
            log.error("Error fetching asset by search value {}", searchId, e);
        }

        return Optional.empty();
    }

    /**
     * Fetches asset history by period
     *
     * @param assetId the id of the asset
     * @param start   the start of the period
     * @param end     the end of the period
     * @return the asset history
     * @throws CoinCapException if there is an error fetching data
     */
    public CoinCapHistoryAssets getAssetHistoryByPeriod(final String assetId,
                                                        final Long start,
                                                        final Long end,
                                                        final String interval) throws CoinCapException {

        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/assets/{id}/history")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("interval", interval)
                .buildAndExpand(Map.of("id", assetId));

        final CoinCapHistoryAssets result = get(uriComponents.toUriString(), CoinCapHistoryAssets.class);
        log.debug("Fetched asset {} history from start {} to end {}: {}", assetId, start, end, result);
        return result;
    }


    /**
     * Fetches assets from CoinCap API
     *
     * @param limit  the limit of assets to fetch
     * @param offset the offset of assets to fetch
     * @return the assets
     * @throws CoinCapException if there is an error fetching data
     */
    public CoinCapAssets getAssets(final int limit, final int offset) throws CoinCapException {
        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/assets")
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .build();
        final CoinCapAssets result = get(uriComponents.toUriString(), CoinCapAssets.class);
        log.debug("Fetched assets from offset {} : {}", offset, result);
        return result;
    }

    /**
     * Fetches asset by id
     *
     * @param assetId the id of the asset
     * @return the asset
     * @throws CoinCapException if there is an error fetching data
     */
    public CoinCapAsset getAsset(final String assetId) throws CoinCapException {
        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/assets/{id}")
                .buildAndExpand(Map.of("id", assetId));
        final CoinCapAsset result = get(uriComponents.toUriString(), CoinCapAsset.class);
        log.debug("Fetched asset with id {} : {}", assetId, result);
        return result;
    }

}
