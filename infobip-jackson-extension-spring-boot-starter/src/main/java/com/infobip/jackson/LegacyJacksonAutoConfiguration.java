package com.infobip.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.infobip.jackson.dynamic.LegacyDynamicHierarchyDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConditionalOnClass(ObjectMapper)
@Deprecated(forRemoval = true)
@Configuration
public class LegacyJacksonAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public LegacyInfobipJacksonModule legacyInfobipJacksonModule(List<LegacyDynamicHierarchyDeserializer<?>> dynamicHierarchyDeserializers) {
        var module = new LegacyInfobipJacksonModule();
        dynamicHierarchyDeserializers.forEach(deserializer -> addDeserializer(module, deserializer));
        return module;
    }

    private <T> void addDeserializer(SimpleModule module, LegacyDynamicHierarchyDeserializer<T> deserializer) {
        module.addDeserializer(deserializer.getHierarchyRootType(), deserializer);
    }
}
