package com.example.demo.mapper;

import com.example.demo.entity.Performance;
import com.example.demo.web.dto.WalletPerformanceResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceMapperTest {

    private static final PerformanceMapper MAPPER = Mappers.getMapper(PerformanceMapper.class);

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2024-12-15T10:00:00Z"),
            Clock.systemDefaultZone().getZone());

    private static final MappingContext CONTEXT = new MappingContext(CLOCK.instant(), "walletId");

    @Test
    void mapEntityToDto() {

        Performance performance = new Performance();
        performance.setTotalUsd(BigDecimal.valueOf(1000));
        performance.setBestAsset("bitcoin");
        performance.setWorstAsset("ethereum");
        performance.setBestPerformance(10f);
        performance.setWorstPerformance(-5f);

        WalletPerformanceResponse walletPerformanceResponse = MAPPER.toDto(performance);

        assertThat(walletPerformanceResponse).isNotNull();
        assertThat(walletPerformanceResponse.getTotal()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(walletPerformanceResponse.getBestAsset()).isEqualTo("bitcoin");
        assertThat(walletPerformanceResponse.getWorstAsset()).isEqualTo("ethereum");
        assertThat(walletPerformanceResponse.getBestPerformance())
                .isEqualTo(BigDecimal.valueOf(10.0));
        assertThat(walletPerformanceResponse.getWorstPerformance())
                .isEqualTo(BigDecimal.valueOf(-5.0));
        assertThat(walletPerformanceResponse.getBestPerformancePercentage())
                .isEqualTo(1000.0f);
        assertThat(walletPerformanceResponse.getWorstPerformancePercentage())
                .isEqualTo(-500.0f);
    }

    @Test
    void mapDtoToEntity() {

        WalletPerformanceResponse walletPerformanceResponse =
                new WalletPerformanceResponse(BigDecimal.valueOf(1000),
                        "bitcoin", BigDecimal.valueOf(10),
                        "ethereum", BigDecimal.valueOf(-5));

        Performance performance = MAPPER.toEntity(walletPerformanceResponse, CONTEXT);

        assertThat(performance).isNotNull();
        assertThat(performance.getTotalUsd()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(performance.getBestAsset()).isEqualTo("bitcoin");
        assertThat(performance.getWorstAsset()).isEqualTo("ethereum");
        assertThat(performance.getBestPerformance()).isEqualTo(10f);
        assertThat(performance.getWorstPerformance()).isEqualTo(-5f);
        assertThat(performance.getId()).isNotNull();
        assertThat(performance.getId().getWalletId()).isEqualTo("walletId");
        assertThat(performance.getId().getUpdatedAt()).isEqualTo(CLOCK.instant());
    }
}
