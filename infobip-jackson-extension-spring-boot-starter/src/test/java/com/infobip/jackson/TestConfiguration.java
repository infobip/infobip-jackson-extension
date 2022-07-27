package com.infobip.jackson;

import com.infobip.jackson.dynamic.DynamicHierarchyDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public DynamicHierarchyDeserializer<FooBar> fooBarDynamicHierarchyDeserializer() {
        return DynamicHierarchyDeserializer.from(FooBarType.class);
    }
}
