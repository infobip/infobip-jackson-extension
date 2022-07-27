package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

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

    @Test
    void shouldRegisterDeserializer() throws JsonProcessingException {
        // given
        String json = "{\"type\":\"FOO\",\"foo\":\"foo\"}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }
}
