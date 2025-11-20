package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.json.JsonMapper;

@AllArgsConstructor
public class SpringBootModuleWiringTest extends TestBase {

    private final JsonMapper jsonMapper;

    @Test
    void shouldRegisterInfobipJacksonModule() {
        // when
        Set<Object> registeredModuleIds = jsonMapper.registeredModules().stream().map(JacksonModule::getRegistrationId).collect(
                Collectors.toSet());

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
