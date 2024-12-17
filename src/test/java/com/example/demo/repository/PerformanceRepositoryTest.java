package com.example.demo.repository;

import com.example.demo.entity.Performance;
import com.example.demo.test.JpaTestProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTestProfile
class PerformanceRepositoryTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2024-12-15T10:00:00Z"),
            Clock.systemDefaultZone().getZone());

    @Autowired
    private PerformanceRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
    }

    @Test
    void testSave() {

        final Performance.PerformanceId id =
                new Performance.PerformanceId("wallet_id", CLOCK.instant());

        final Performance performance = new Performance();
        performance.setId(id);
        performance.setTotalUsd(new BigDecimal("9999999999.00007248741714484778"));
        performance.setBestAsset("BTC");
        performance.setBestPerformance(10f);
        performance.setWorstAsset("ETH");
        performance.setWorstPerformance(-10f);

        Performance performanceSaved = repository.save(performance);
        assertThat(performanceSaved).isEqualTo(performance);

        final Optional<Performance> savedPerformanceOptional =
                repository.findById(performance.getId());

        var extractingId = assertThat(savedPerformanceOptional)
                .isPresent()
                .hasValue(performance)
                .get()
                .extracting("id");

        extractingId.isEqualTo(id);

        extractingId.extracting("walletId")
                .isEqualTo("wallet_id");

        extractingId.extracting("updatedAt")
                .isEqualTo(CLOCK.instant());
    }

}
