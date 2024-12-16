package com.example.demo.web.controller;

import com.example.demo.exception.ValidationException;
import com.example.demo.service.WalletService;
import com.example.demo.service.parser.ParseException;
import com.example.demo.service.parser.RequestedAssetsParserService;
import com.example.demo.web.api.WalletApi;
import com.example.demo.web.dto.RequestedAsset;
import com.example.demo.web.dto.WalletPerformanceResponse;
import com.example.demo.web.exception.NotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for the wallet API.
 */

@RestController
public class WalletController implements WalletApi {

    private final RequestedAssetsParserService requestedAssetsParserService;
    private final WalletService walletPerformanceService;

    public WalletController(
            final RequestedAssetsParserService requestedAssetsParserService,
            final WalletService walletPerformanceService) {
        this.requestedAssetsParserService = requestedAssetsParserService;
        this.walletPerformanceService = walletPerformanceService;
    }

    /**
     * Update the performance of the wallet.
     *
     * @param walletId the wallet ID
     * @param requestedAssets the requested assets
     * @return the wallet performance
     * @throws NotFoundException if the wallet is not found
     * @throws ParseException if the requested assets are invalid
     */
    public WalletPerformanceResponse updatePerformance(final String walletId, final String requestedAssets) throws NotFoundException, ParseException {

        // Parse the requested assets
        final List<RequestedAsset> requestedAssetList = requestedAssetsParserService.parseRequestedAssets(requestedAssets);

        // Get the performance
        return walletPerformanceService.updatePerformance(walletId, requestedAssetList);
    }

    /**
     * Get the performance of the wallet.
     *
     * @param walletId the wallet ID
     * @return the wallet performance
     * @throws NotFoundException if the wallet is not found
     * @throws ValidationException if the wallet is invalid
     */
    public WalletPerformanceResponse getPerformance(final String walletId) throws NotFoundException, ValidationException {
        return walletPerformanceService.getPerformance(walletId);
    }
}
