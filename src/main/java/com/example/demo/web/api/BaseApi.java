package com.example.demo.web.api;

import com.example.demo.web.exception.BadRequestException;
import com.example.demo.web.exception.InternalServerErrorException;
import com.example.demo.web.exception.NotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

/**
 * Base API interface with common configurations
 * for OpenAPI documentation.
 */

@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad request",
                content = {
                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = BadRequestException.class))
                }),
        @ApiResponse(responseCode = "404", description = "Request resource was not found",
                content = {
                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = NotFoundException.class))
                }),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = {
                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = InternalServerErrorException.class))
                })

})
public interface BaseApi {

    String PATH = "/api";

    default String getPath() {
        return PATH;
    }
}
