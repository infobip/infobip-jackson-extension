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
class NamedPresentPropertyDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeFooAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json = "{'fooProperty':'foo'}";

        // when
        NamedFooBar actual = objectMapper.readValue(json, NamedFooBar.class);

        // then
        then(actual).isEqualTo(new NamedFoo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooBarFromSerializedFoo() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new NamedFoo("foo"));

        // when
        NamedFooBar actual = objectMapper.readValue(json, NamedFooBar.class);

        // then
        then(actual).isEqualTo(new NamedFoo("foo"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json = "{'barProperty':'bar'}";

        // when
        NamedFooBar actual = objectMapper.readValue(json, NamedFooBar.class);

        // then
        then(actual).isEqualTo(new NamedBar("bar"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromSerializedBar() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new NamedBar("bar"));

        // when
        NamedFooBar actual = objectMapper.readValue(json, NamedFooBar.class);

        // then
        then(actual).isEqualTo(new NamedBar("bar"));
    }

    @Test
    void shouldDeserializeListOfFooBars() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(Arrays.asList(new NamedFoo("foo"), new NamedBar("bar")));

        // when
        List<NamedFooBar> actual = objectMapper.readValue(json, new TypeReference<List<NamedFooBar>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new NamedFoo("foo"), new NamedBar("bar")));
    }

    @Test
    void shouldDeserializeBarWithMultipleMatchingProperties() throws JsonProcessingException {
        // given
        String json = "{'barProperty':'', 'fooProperty': 'foo'}";

        // when
        NamedFooBar actual = objectMapper.readValue(json, NamedFooBar.class);

        // then
        then(actual).isEqualTo(new NamedFoo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() throws JsonProcessingException {
        // given
        String json = "{'fooProperty':'foo'}";

        // when
        NamedFoo actual = objectMapper.readValue(json, NamedFoo.class);

        // then
        then(actual).isEqualTo(new NamedFoo("foo"));
    }

    @Getter
    @AllArgsConstructor
    enum FooBarType implements NamedPropertyTypeProvider {
        FOO(NamedFoo.class, "fooProperty"),
        BAR(NamedBar.class, "barProperty");

        private final Class<? extends NamedFooBar> type;
        private final String jsonPropertyName;
    }

    interface NamedFooBar extends PresentPropertyJsonHierarchy<FooBarType> {

    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class NamedFoo implements NamedFooBar {

        private final String fooProperty;
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class NamedBar implements NamedFooBar {

        private final String barProperty;
    }
}