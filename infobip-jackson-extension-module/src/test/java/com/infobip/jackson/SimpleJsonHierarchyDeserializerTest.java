package com.infobip.jackson;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

@AllArgsConstructor
class SimpleJsonHierarchyDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeFooAsFooBarFromJson() {
        // given
        String json ="{'type':'FOO','foo':'foo'}";

        // when
        var actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooBarFromSerializedFoo() {
        // given
        String json = jsonMapper.writeValueAsString(new Foo("foo"));

        // when
        var actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromJson() {
        // given
        String json ="{'type':'BAR','bar':'bar'}";

        // when
        var actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromSerializedBar() {
        // given
        String json = jsonMapper.writeValueAsString(new Bar("bar"));

        // when
        var actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeListOfFooBars() {
        // given
        String json = jsonMapper.writeValueAsString(Arrays.asList(new Foo("foo"), new Bar("bar")));

        // when
        var actual = jsonMapper.readValue(json, new TypeReference<List<FooBar>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new Foo("foo"), new Bar("bar")));
    }

    @Test
    void shouldHandleUnknownType() {
        // given
        String json ="{'type':'baz'}";

        // when
        var actual = catchThrowable(() -> jsonMapper.readValue(json, FooBar.class));

        // then
        then(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(
                            "No enum constant " + getClass().getName() + ".FooBarType.baz");
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() {
        // given
        String json ="{'type':'FOO','foo':'foo'}";

        // when
        var actual = jsonMapper.readValue(json, Foo.class);

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
    }
}
