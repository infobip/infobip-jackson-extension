package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class PresentPropertyDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeFooAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json ="{'foo':'foo'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooBarFromSerializedFoo() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(new Foo("foo"));

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json ="{'bar':'bar'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromSerializedBar() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(new Bar("bar"));

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeListOfFooBars() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(Arrays.asList(new Foo("foo"), new Bar("bar")));

        // when
        List<FooBar> actual = objectMapper.readValue(json, new TypeReference<List<FooBar>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new Foo("foo"), new Bar("bar")));
    }

    @Test
    void shouldDeserializeBarWithMultipleMatchingProperties() throws JsonProcessingException {
        // given
        String json ="{'bar':'', 'foo': 'foo'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() throws JsonProcessingException {
        // given
        String json ="{'foo':'foo'}";

        // when
        Foo actual = objectMapper.readValue(json, Foo.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    interface FooBar extends PresentPropertyJsonHierarchy<FooBarType> {
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class Foo implements FooBar {
        private final String foo;
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class Bar implements FooBar {
        private final String bar;
    }

    @Getter
    @AllArgsConstructor
    enum FooBarType implements TypeProvider {
        FOO(Foo.class),
        BAR(Bar.class);

        private final Class<? extends FooBar> type;
    }
}