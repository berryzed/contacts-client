package com.daou.contacts.client.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate getCustomRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2_000); // 연결
        requestFactory.setReadTimeout(20_000); // 읽기

        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(200) // 최대 연결
                .setMaxConnPerRoute(20)
                .build();

        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }
}
