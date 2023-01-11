package com.infobip.jackson;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
class LowerCaseTypeSimpleJsonHierarchyTest extends TestBase {

    @Test
    void shouldDeserializeFooAsFooBarFromJson() throws JsonProcessingException {
        // given
        String json ="{'type':'foo','foo':'foo'}";

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
        String json ="{'type':'bar','bar':'bar'}";

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
    void shouldHandleUnknownType() {
        // given
        String json ="{'type':'baz'}";

        // when
        Throwable actual = catchThrowable(() -> objectMapper.readValue(json, FooBar.class));

        // then
        then(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(
                            "No enum constant " + getClass().getName() + ".FooBarType.BAZ");
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() throws JsonProcessingException {
        // given
        String json ="{'type':'foo','foo':'foo'}";

        // when
        Foo actual = objectMapper.readValue(json, Foo.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    @JsonTypePropertyValue(inUpperCase = false)
    interface FooBar extends SimpleJsonHierarchy<FooBarType> {
        FooBarType getType();
    }

    record Foo(String foo) implements FooBar {

        public FooBarType getType() {
                return FooBarType.FOO;
            }

    }

    record Bar(String bar) implements FooBar {

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
