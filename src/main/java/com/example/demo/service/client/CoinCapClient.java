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
                            throw new CoinCapException("Error requesting url %s".formatted(template), null);
                        })
                .body(clazz);
    }

    /**
     * Fetches asset by symbol
     *
     * @param symbol the symbol of the asset
     * @return the asset
     * @throws CoinCapException if there is an error fetching data
     */
    public Optional<CoinCapAssets> getAssetBySymbol(final String symbol) throws CoinCapException {

        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/assets")
                .queryParam("search", symbol)
                .queryParam("offset", 0)
                .queryParam("limit", 1)
                .build();

        try {
            final CoinCapAssets result = get(uriComponents.toUriString(), CoinCapAssets.class);
            log.debug("Fetched assets from offset {} limit {}: {}", 1, 1, result);
            return Optional.of(result);
        } catch (CoinCapException e) {
            log.error("Error fetching asset by symbol {}", symbol, e);
        }

        return Optional.empty();
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
