package com.example.demo.service.parser;

import com.example.demo.exception.OffendingValue;
import com.example.demo.web.dto.RequestedAsset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service to parse the requested assets from the input string.
 * symbol|quantity|price
 * BTC,0.12345,37870.5058
 * ETH,4.89532,2004.9774
 * LTC,10.12345,175.5058
 * return List<RequestedAsset>
 */

@Service
@Slf4j
public class RequestedAssetsParserService {

    private static final String NEW_LINE = "\\r?\\n";

    private String getContent(final String part) {
        log.debug("getContent: part={}", part);
        return Optional.ofNullable(part).map(String::trim).orElse("");
    }

    /**
     * Parse the requested assets from the input string.
     *
     * @param requestedAssets the input string
     * @return the set of requested assets
     */
    public Set<RequestedAsset> parseRequestedAssets(String requestedAssets) throws ParseException {

        final Set<RequestedAsset> assets = new LinkedHashSet<>();

        if (requestedAssets == null || requestedAssets.isEmpty()) {
            final OffendingValue offendingValue = OffendingValue
                    .builder()
                    .message("The input string is empty")
                    .field("requestedAssets")
                    .invalidValue(requestedAssets)
                    .build();
            throw new ParseException(offendingValue, "Invalid requested assets");
        }

        // Split the input by new lines
        String[] lines = requestedAssets.split(NEW_LINE);

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String symbol = getContent(parts[0]);
                BigDecimal quantity = new BigDecimal(getContent(parts[1]));
                BigDecimal price = new BigDecimal(getContent(parts[2]));
                assets.add(RequestedAsset.builder()
                        .symbol(symbol)
                        .quantity(quantity)
                        .price(price)
                        .build());
            } else {
                final OffendingValue offendingValue = OffendingValue
                        .builder()
                        .message("The input asset is invalid")
                        .field("requestedAssets")
                        .invalidValue(line)
                        .build();
                throw new ParseException(offendingValue, "The asset '" + line + "' is invalid. It should have 3 fields separated by commas");
            }
        }

        return assets;
    }
}