package com.example.demo.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Requested asset DTO.
 */

@Builder
@Data
@EqualsAndHashCode(of = {"symbol"})
public final class RequestedAsset {
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
}