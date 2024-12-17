package com.example.demo.web.controller;

import com.example.demo.service.WalletService;
import com.example.demo.service.parser.ParseException;
import com.example.demo.service.parser.RequestedAssetsParserService;
import com.example.demo.web.api.WalletApi;
import com.example.demo.web.dto.RequestedAsset;
import com.example.demo.web.dto.WalletPerformanceResponse;
import com.example.demo.web.exception.NotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Controller for the wallet API.
 */

@RestController
public class WalletController implements WalletApi {

    private final RequestedAssetsParserService requestedAssetsParserService;
    private final WalletService walletService;

    public WalletController(
            final RequestedAssetsParserService requestedAssetsParserService,
            final WalletService walletPerformanceService) {
        this.requestedAssetsParserService = requestedAssetsParserService;
        this.walletService = walletPerformanceService;
    }

    /**
     * Update the performance of the wallet.
     *
     * @param walletId        the wallet ID
     * @param requestedAssets the requested assets
     * @return the wallet performance
     * @throws NotFoundException if the wallet is not found
     * @throws ParseException    if the requested assets are invalid
     */
    public WalletPerformanceResponse updatePerformance(final String walletId,
                                                       final String requestedAssets,
                                                       final ZonedDateTime zonedDateTime) throws ParseException {

        // Parse the requested assets
        final Set<RequestedAsset> requestedAssetList =
                requestedAssetsParserService.parseRequestedAssets(requestedAssets);

        final Instant performanceAt = Optional.ofNullable(zonedDateTime)
                .map(ZonedDateTime::toInstant)
                .orElse(null);

        // Get the performance
        return walletService.updatePerformance(walletId, requestedAssetList, performanceAt);
    }
}
