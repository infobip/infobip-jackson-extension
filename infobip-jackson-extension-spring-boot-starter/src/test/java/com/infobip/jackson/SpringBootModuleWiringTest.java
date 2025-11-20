package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;


import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
public class SpringBootModuleWiringTest extends TestBase {

    private final JsonMapper jsonMapper;

    @Test
    void shouldRegisterInfobipJacksonModule() {
        // when
        Set<Object> registeredModuleIds = jsonMapper.getRegisteredModuleIds();

        // then
        then(registeredModuleIds).contains("com.infobip.jackson.InfobipJacksonModule");
    }

    @Test
    void shouldRegisterDeserializer() {
        // given
        String json = "{\"type\":\"FOO\",\"foo\":\"foo\"}";

        // when
        FooBar actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }
}
