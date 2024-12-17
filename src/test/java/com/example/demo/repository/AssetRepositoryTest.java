package com.example.demo.repository;

import com.example.demo.entity.Asset;
import com.example.demo.test.JpaTestProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTestProfile
class AssetRepositoryTest {

    private static final String JSON = """
            {
                 "id": "bitcoin",
                 "rank": "1",
                 "symbol": "BTC",
                 "name": "Bitcoin",
                 "supply": "19796059.0000000000000000",
                 "maxSupply": "21000000.0000000000000000",
                 "marketCapUsd": "2000412495530.5447598064915930",
                 "volumeUsd24Hr": "10040684761.2709351284864753",
                 "priceUsd": "101051.0473589993220270",
                 "changePercent24Hr": "-0.4034808436340276",
                 "vwap24Hr": "101643.7971494658380754",
                 "explorer": "https://blockchain.info/"
             }
            """;

    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Autowired
    private AssetRepository repository;

    @BeforeAll
    static void beforeAll() {
        OBJECTMAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
    }

    private Asset buildAsset() throws JsonProcessingException {
        return OBJECTMAPPER.readValue(JSON, Asset.class);
    }

    @Test
    void saveAsset() throws JsonProcessingException {
        final Asset asset = buildAsset();
        Asset assetMono = repository.save(asset);
        assertThat(assetMono).isEqualTo(asset);

        final Optional<Asset> savedAssetOptional = repository.findById(asset.getId());
        assertThat(savedAssetOptional)
                .isPresent()
                .hasValue(asset);
    }
}
