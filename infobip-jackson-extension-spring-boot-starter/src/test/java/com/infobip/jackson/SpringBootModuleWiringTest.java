package com.infobip.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class SpringBootModuleWiringTest extends TestBase {

    private final ObjectMapper objectMapper;

    @Test
    void shouldRegisterInfobipJacksonModule() {
        // when
        Set<Object> registeredModuleIds = objectMapper.getRegisteredModuleIds();

        // then
        then(registeredModuleIds).contains("com.infobip.jackson.InfobipJacksonModule");
    }
}
