package com.example.demo.config;

import com.example.demo.config.properties.CoinCapProperties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * This class configures the HTTP client
 */

@Configuration
public class HttpConfiguration {

    private final CoinCapProperties coinCapProperties;

    public HttpConfiguration(final CoinCapProperties coinCapProperties) {
        this.coinCapProperties = coinCapProperties;
    }

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return HttpClientBuilder.create().build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(final CloseableHttpClient httpClient) {

        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        clientHttpRequestFactory.setConnectionRequestTimeout(coinCapProperties.getReadTimeout());
        clientHttpRequestFactory.setConnectTimeout(coinCapProperties.getConnectionTimeout());
        return clientHttpRequestFactory;
    }

    @Bean("coinCapRestClient")
    public RestClient restClient(final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {

        return RestClient.builder()
                .baseUrl(coinCapProperties.getUrl())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("Authorization",
                            "Bearer " + coinCapProperties.getApiKey());
                    httpHeaders.add("Accept", "application/json");
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Accept-Encoding", "gzip");
                })
                .requestFactory(clientHttpRequestFactory)
                .build();
    }
}
