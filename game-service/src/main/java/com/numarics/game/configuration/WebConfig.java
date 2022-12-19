package com.numarics.game.configuration;

import com.numarics.game.converter.GameToGameDetailsDtoConverter;
import com.numarics.game.converter.GameToStartGameResponseDtoConverter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        var restTemplate = new RestTemplate();
        var httpClient = HttpClientBuilder.create().build();
        var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new GameToGameDetailsDtoConverter());
        registry.addConverter(new GameToStartGameResponseDtoConverter());
    }
}
