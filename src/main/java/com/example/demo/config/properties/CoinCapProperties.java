package com.example.demo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.coincap")
@Data
public class CoinCapProperties {

    private String url;
    private String apiKey;
    private int assetsPageSize = 100;

    private int connectionTimeout = 5000;
    private int readTimeout = 5000;
    private int writeTimeout = 5000;
    private int keepAliveDuration = 5000;
    private int maxIdleConnections = 10;
}
