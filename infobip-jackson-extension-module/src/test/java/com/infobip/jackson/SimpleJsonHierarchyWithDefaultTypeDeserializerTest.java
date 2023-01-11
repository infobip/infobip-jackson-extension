package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
class SimpleJsonHierarchyWithDefaultTypeDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeToDefaultType() throws JsonProcessingException {
        // given
        String json = "{'foo':'foo'}";

        // when
        var actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    interface FooBar extends SimpleJsonHierarchy<FooBarType> {

    }

    record Foo(String foo) implements FooBar {

        @Override
        public FooBarType getType() {
            return FooBarType.FOO;
        }

    }

    record Bar(String bar) implements FooBar {

        @Override
        public FooBarType getType() {
            return FooBarType.BAR;
        }

    }

    @Getter
    @AllArgsConstructor
    enum FooBarType implements TypeProvider<FooBar> {
        FOO(Foo.class),
        BAR(Bar.class);

        private final Class<? extends FooBar> type;

        @Override
        public Optional<Class<? extends FooBar>> getDefaultType() {
            return Optional.of(Foo.class);
        }
    }

}
