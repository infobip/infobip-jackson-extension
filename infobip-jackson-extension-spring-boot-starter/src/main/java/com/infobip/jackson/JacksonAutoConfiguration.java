package com.infobip.jackson;

import java.util.List;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.infobip.jackson.dynamic.DynamicHierarchyDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public InfobipJacksonModule infobipJacksonModule(List<DynamicHierarchyDeserializer<?>> dynamicHierarchyDeserializers) {
        InfobipJacksonModule module = new InfobipJacksonModule();
        dynamicHierarchyDeserializers.forEach(deserializer -> addDeserializer(module, deserializer));
        return module;
    }

    private <T> void addDeserializer(SimpleModule module, DynamicHierarchyDeserializer<T> deserializer) {
        module.addDeserializer(deserializer.getHierarchyRootType(), deserializer);
    }
}
