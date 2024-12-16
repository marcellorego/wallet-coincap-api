package com.example.demo.config;

import com.example.demo.config.properties.SchedulerProperties;
import com.example.demo.scheduler.UpdateAssetsScheduleTask;
import com.example.demo.service.CoinCapService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Clock;

/**
 * The type Scheduler configuration.
 * Enables scheduling and creates a task scheduler
 */

@Configuration
@EnableScheduling
public class SchedulerConfiguration {

    /**
     * Update assets schedule job update assets schedule job.
     *
     * @param defaultClock   the default clock
     * @param coinCapService the coin cap service
     * @return the update assets schedule job
     */
    @Bean
    @ConditionalOnProperty(
            value = "scheduler.enabled", havingValue = "true", matchIfMissing = true
    )
    public UpdateAssetsScheduleTask updateAssetsScheduleJob(final Clock defaultClock,
                                                            final CoinCapService coinCapService) {
        return new UpdateAssetsScheduleTask(defaultClock, coinCapService);
    }

    /**
     * Returns a concurrent task scheduler
     *
     * @return The {@link TaskScheduler}
     */
    @Bean
    public TaskScheduler taskScheduler(final SchedulerProperties schedulerProperties) {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(schedulerProperties.getPoolSize());
        threadPoolTaskScheduler.setThreadNamePrefix(schedulerProperties.getThreadNamePrefix());
        return threadPoolTaskScheduler;
    }
}