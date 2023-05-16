package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.var;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
class SimpleJsonHierarchyWithDefaultTypeDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeToDefaultType() throws JsonProcessingException {
        // given
        String json ="{'foo':'foo'}";

        // when
        var actual = objectMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    interface FooBar extends SimpleJsonHierarchy<FooBarType> {
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
