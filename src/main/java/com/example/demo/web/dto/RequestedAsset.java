package com.example.demo.web.dto;

import java.math.BigDecimal;

/**
 * Requested asset DTO.
 */

public record RequestedAsset(String symbol, BigDecimal quantity, BigDecimal price) {
}