package com.numarics.game.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppProperties {

    @Value("${player-service.host}")
    private String playerServiceHost;

    @Value("${player-service.port}")
    private String playerServicePort;

    @Value("${player-service.entryPoint}")
    private String playerServiceEntryPoint;
}
