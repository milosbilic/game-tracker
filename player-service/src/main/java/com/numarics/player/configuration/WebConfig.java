package com.numarics.player.configuration;

import com.numarics.player.converter.PlayerToPlayerDetailsDtoConverter;
import com.numarics.player.converter.PlayerToRegisterPlayerResponseDtoConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PlayerToRegisterPlayerResponseDtoConverter());
        registry.addConverter(new PlayerToPlayerDetailsDtoConverter());
    }
}
