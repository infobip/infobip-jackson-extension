package com.infobip.jackson.dynamic;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.infobip.jackson.TestBase;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicHierarchyDeserializerTest extends TestBase {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        DynamicHierarchyModule module = new DynamicHierarchyModule();
        List<JsonValueToJavaTypeJacksonMapping<FooBar>> jsonValueToJavaTypeJacksonMappings = Arrays.asList(
            new JsonValueToJavaTypeJacksonMapping<>(FooBarType.BAR.name(), Bar.class),
            new JsonValueToJavaTypeJacksonMapping<>(FooBarType.FOO.name(), Foo.class));

        DynamicHierarchyDeserializer<FooBar> deserializer = new DynamicHierarchyDeserializer<>(FooBar.class, jsonValueToJavaTypeJacksonMappings);
        module.addDeserializer(FooBar.class, deserializer);
        this.objectMapper.registerModule(module);
    }

    static class DynamicHierarchyModule extends SimpleModule {

    }

    @Test
    void shouldDeserializeFooAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json = "{'type':'FOO','foo':'foo'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooBarFromSerializedFoo() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Foo("foo"));

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json = "{'type':'BAR','bar':'bar'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromSerializedBar() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Bar("bar"));

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeListOfFooBars() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(Arrays.asList(new Foo("foo"), new Bar("bar")));

        // when
        List<FooBar> actual = objectMapper.readValue(json, new TypeReference<List<FooBar>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new Foo("foo"), new Bar("bar")));
    }

    @Test
    void shouldHandleUnknownType() {
        // given
        String json = "{'type':'baz'}";

        // when
        Throwable actual = catchThrowable(() -> objectMapper.readValue(json, FooBar.class));

        // then
        then(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("No java type mapping specified for json value: baz");
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() throws JsonProcessingException {
        // given
        String json = "{'type':'FOO','foo':'foo'}";

        // when
        Foo actual = objectMapper.readValue(json, Foo.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    interface FooBar {

        FooBarType getType();

    }

    @Value
    static class Foo implements FooBar {

        private final String foo;
        private final FooBarType type = FooBarType.FOO;

    }

    @Value
    static class Bar implements FooBar {

        private final String bar;
        private final FooBarType type = FooBarType.BAR;

    }

    enum FooBarType {
        FOO,
        BAR;
    }
}
