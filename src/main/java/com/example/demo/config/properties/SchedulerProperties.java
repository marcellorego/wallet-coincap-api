package com.example.demo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scheduler")
@Data
public class SchedulerProperties {

    private boolean enabled = true;
    private String cronExpression = "0 * * * * *";
    private int poolSize = 1;
    private String threadNamePrefix = "ThreadPoolTaskScheduler-";
}
