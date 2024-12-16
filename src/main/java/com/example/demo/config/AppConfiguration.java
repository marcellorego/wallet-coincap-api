package com.example.demo.config;

import com.example.demo.config.properties.CoinCapProperties;
import com.example.demo.config.properties.SchedulerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@EnableConfigurationProperties( { CoinCapProperties.class, SchedulerProperties.class, CoinCapProperties.class } )
public class AppConfiguration {

    /**
     * The default time zone to be used by the application.
     * It can be overridden, for example, for testing.
     * @return The {@link ZoneId} to use
     */
    @Bean
    public ZoneId zoneId() {
        return ZoneId.systemDefault();
    }

    /**
     * The default clock to be used by the application.
     * It can be overridden, for example, for testing.
     * @param zoneId Zone Id
     * @return The {@link Clock} to use
     */
    @Bean
    public Clock defaultClock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }
}
