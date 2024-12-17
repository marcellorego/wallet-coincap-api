package com.example.demo.service.parser;

import com.example.demo.exception.OffendingValue;
import com.example.demo.web.dto.RequestedAsset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestedAssetsParserServiceTest {

    private final RequestedAssetsParserService service = new RequestedAssetsParserService();

    @Test
    void parseNullString() {
        assertThatThrownBy(() -> service.parseRequestedAssets(null))
                .isInstanceOf(ParseException.class)
                .hasMessage("Invalid requested assets")
                .extracting("offendingValues")
                .isEqualTo(Set.of(
                        OffendingValue
                                .builder()
                                .message("Invalid requested assets")
                                .field("requestedAssets")
                                .invalidValue(null)
                                .build()
                ));
    }

    @Test
    void parseEmptyString() {
        assertThatThrownBy(() -> service.parseRequestedAssets(""))
                .isInstanceOf(ParseException.class)
                .hasMessage("Invalid requested assets")
                .extracting("offendingValues")
                .isEqualTo(Set.of(
                        OffendingValue
                                .builder()
                                .message("The input string is empty")
                                .field("requestedAssets")
                                .invalidValue("")
                                .build()
                ));
    }

    @Test
    void parseInvalidAsset() {
        String invalidAssets = "asset1,0.12345";
        assertThatThrownBy(() -> service.parseRequestedAssets(invalidAssets))
                .isInstanceOf(ParseException.class)
                .hasMessage("The asset 'asset1,0.12345' is invalid. It should have 3 fields separated by commas");
    }

    @Test
    void parseInvalidAsset2() {
        String invalidAssets = "asset1,0.12345,37870.5058,123";
        assertThatThrownBy(() -> service.parseRequestedAssets(invalidAssets))
                .isInstanceOf(ParseException.class)
                .hasMessage("The asset 'asset1,0.12345,37870.5058,123' is invalid. It should have 3 fields separated by commas");
    }

    @Test
    void parseOneValidAsset() throws ParseException {

        String validAssets = "asset1,0.12345,37870.5058";

        Set<RequestedAsset> result = service.parseRequestedAssets(validAssets);
        assertThat(result)
                .isNotEmpty()
                .hasSize(1);

        RequestedAsset asset1 = result.iterator().next();
        assertThat(asset1)
                .hasFieldOrPropertyWithValue("symbol", "asset1")
                .hasFieldOrPropertyWithValue("quantity", BigDecimal.valueOf(0.12345))
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(37870.5058));
    }

    @Test
    void parseMultipleValidAssets() throws ParseException {

        String validAssets = "asset1,0.12345,37870.5058\nasset2,0.12345,37870.5058\nasset3,0.12345,37870.5058";

        Set<RequestedAsset> result = service.parseRequestedAssets(validAssets);
        assertThat(result)
                .isNotEmpty()
                .hasSize(3);

        Iterator<RequestedAsset> iterator = result.iterator();

        RequestedAsset asset1 = iterator.next();
        assertThat(asset1)
                .hasFieldOrPropertyWithValue("symbol", "asset1")
                .hasFieldOrPropertyWithValue("quantity", BigDecimal.valueOf(0.12345))
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(37870.5058));

        RequestedAsset asset2 = iterator.next();
        assertThat(asset2)
                .hasFieldOrPropertyWithValue("symbol", "asset2")
                .hasFieldOrPropertyWithValue("quantity", BigDecimal.valueOf(0.12345))
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(37870.5058));

        RequestedAsset asset3 = iterator.next();
        assertThat(asset3)
                .hasFieldOrPropertyWithValue("symbol", "asset3")
                .hasFieldOrPropertyWithValue("quantity", BigDecimal.valueOf(0.12345))
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(37870.5058));
    }

}