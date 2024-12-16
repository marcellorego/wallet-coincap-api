package com.example.demo.scheduler;

import com.example.demo.service.CoinCapService;
import com.example.demo.util.ElapsedTimeWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Scheduled job to update assets.
 */

@Slf4j
public class UpdateAssetsScheduleTask {

    private static final AtomicBoolean FETCH_SELECTOR = new AtomicBoolean(false);

    private final Clock defaultClock;
    private final CoinCapService coinCapService;

    public UpdateAssetsScheduleTask(final Clock defaultClock,
                                    final CoinCapService coinCapService) {
        this.defaultClock = defaultClock;
        this.coinCapService = coinCapService;
    }

    /**
     * Run the scheduled job to update assets based on the cron expression.
     */
    @Scheduled(cron = "${scheduler.cronExpression}")
    public void runUpdateAssets() {
        if (FETCH_SELECTOR.compareAndSet(false, true)) {
            updateAssets();
        } else {
            log.warn("updateAssets :: There is already a previous process updating assets. Skipping to the next scheduled timeout!");
        }
    }

    /**
     * Update assets.
     */
    private void updateAssets() {

        final ElapsedTimeWatcher watcher = ElapsedTimeWatcher.start();
        try {
            log.info("updateAssets :: Started updating assets at: {}", defaultClock.instant());
            coinCapService.updateAssets();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            FETCH_SELECTOR.set(false);
            final String end = watcher.elapsedTimeInSeconds();
            log.info("updateAssets :: Finished updating assets at: {}", defaultClock.instant());
            log.info("updateAssets :: Elapsed time: {} seconds", end);
        }
    }
}
