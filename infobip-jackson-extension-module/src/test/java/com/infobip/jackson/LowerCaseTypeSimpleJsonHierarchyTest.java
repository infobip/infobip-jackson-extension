package com.infobip.jackson;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

@AllArgsConstructor
class LowerCaseTypeSimpleJsonHierarchyTest extends TestBase {

    @Test
    void shouldDeserializeFooAsFooBarFromJson() {
        // given
        String json ="{'type':'foo','foo':'foo'}";

        // when
        FooBar actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeFooAsFooBarFromSerializedFoo() {
        // given
        String json = jsonMapper.writeValueAsString(new Foo("foo"));

        // when
        FooBar actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromJson() {
        // given
        String json ="{'type':'bar','bar':'bar'}";

        // when
        FooBar actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeBarAsFooBarFromSerializedBar() {
        // given
        String json = jsonMapper.writeValueAsString(new Bar("bar"));

        // when
        FooBar actual = jsonMapper.readValue(json, FooBar.class);

        // then
        then(actual).isEqualTo(new Bar("bar"));
    }

    @Test
    void shouldDeserializeListOfFooBars() {
        // given
        String json = jsonMapper.writeValueAsString(Arrays.asList(new Foo("foo"), new Bar("bar")));

        // when
        List<FooBar> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new Foo("foo"), new Bar("bar")));
    }

    @Test
    void shouldHandleUnknownType() {
        // given
        String json ="{'type':'baz'}";

        // when
        Throwable actual = catchThrowable(() -> jsonMapper.readValue(json, FooBar.class));

        // then
        then(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(
                            "No enum constant " + getClass().getName() + ".FooBarType.BAZ");
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() {
        // given
        String json ="{'type':'foo','foo':'foo'}";

        // when
        Foo actual = jsonMapper.readValue(json, Foo.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @JsonTypePropertyValue(inUpperCase = false)
    interface FooBar extends SimpleJsonHierarchy<FooBarType> {
        FooBarType getType();
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

        @JsonValue
        String getValue() {
            return toString().toLowerCase();
        }
    }
}
