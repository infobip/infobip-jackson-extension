package com.infobip.jackson;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public InfobipJacksonModule infobipJacksonModule() {
        return new InfobipJacksonModule();
    }
}