package com.example.demo.web.api;


import com.example.demo.exception.ValidationException;
import com.example.demo.web.dto.WalletPerformanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.ZonedDateTime;

@Tag(name = "wallet", description = "Wallet API")
@RequestMapping(path = WalletApi.PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
public interface WalletApi extends BaseApi {

    String PATH = BaseApi.PATH + "/wallet";

    @Operation(summary = "Returns the updated total financial value of provided wallet with related performance", description = "Returns a wallet performance response",
            operationId = "updatePerformance", tags = "wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "operation accepted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WalletPerformanceResponse.class)))

    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "/{walletId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    WalletPerformanceResponse updatePerformance(
            @org.springframework.web.bind.annotation.PathVariable(value = "walletId") String walletId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(value = "BTC,0.12345,37870.5058\nETH,4.89532,2004.9774\nBNB,1.05,750.244144127")}
                    ))
            @org.springframework.web.bind.annotation.RequestBody String requestedAssets,
            @org.springframework.web.bind.annotation.RequestParam(value = "date", required = false) ZonedDateTime zonedDateTime) throws ValidationException;
}
