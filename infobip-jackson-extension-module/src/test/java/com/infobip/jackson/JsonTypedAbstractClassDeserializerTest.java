package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class JsonTypedAbstractClassDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeFooAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json ="{'type':'FOO','foo':'foo'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json ="{'type':'BAR','bar':'bar'}";

        // when
        FooBar actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @JsonTypeResolveWith(FooBarJsonTypeResolver.class)
    static abstract class FooBar {
        abstract FooBarType getType();
    }

    static class FooBarJsonTypeResolver implements JsonTypeResolver {
        @Override
        public Class<?> resolve(Map<String, Object> json) {
            return FooBarType.valueOf(json.get("type").toString()).getType();
        }
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class Foo extends FooBar {
        private final String foo;
        private final FooBarType type = FooBarType.FOO;
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class Bar extends FooBar {
        private final String bar;
        private final FooBarType type = FooBarType.BAR;
    }

    @Getter
    @AllArgsConstructor
    enum FooBarType {
        FOO(Foo.class),
        BAR(Bar.class);

        private final Class<? extends FooBar> type;
    }
}