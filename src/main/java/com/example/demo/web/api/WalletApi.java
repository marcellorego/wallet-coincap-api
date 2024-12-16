package com.example.demo.web.api;


import com.example.demo.exception.ValidationException;
import com.example.demo.web.dto.WalletPerformanceResponse;
import com.example.demo.web.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "wallet", description = "Wallet API")
@RequestMapping(path = WalletApi.PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
public interface WalletApi extends BaseApi {

    String PATH = BaseApi.PATH + "/wallet";

    @Operation(summary = "Returns the last calculated financial performance of provided wallet", description = "Returns a wallet performance response",
            operationId = "getPerformance", tags = "wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation accepted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WalletPerformanceResponse.class)))

    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{walletId}")
    WalletPerformanceResponse getPerformance(
            @org.springframework.web.bind.annotation.PathVariable(value = "walletId") String walletId) throws NotFoundException, ValidationException;


    @Operation(summary = "Returns the updated total financial value of provided wallet with related performance", description = "Returns a wallet performance response",
            operationId = "updatePerformance", tags = "wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "operation accepted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WalletPerformanceResponse.class)))

    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "/{walletId}/update", consumes = MediaType.TEXT_PLAIN_VALUE)
    WalletPerformanceResponse updatePerformance(
            @org.springframework.web.bind.annotation.PathVariable(value = "walletId") String walletId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(value = "BTC,0.12345,37870.5058\nETH,4.89532,2004.9774\nBNB,1.05,750.244144127")}
                    ))
            @org.springframework.web.bind.annotation.RequestBody String requestedAssets) throws NotFoundException, ValidationException;
}
