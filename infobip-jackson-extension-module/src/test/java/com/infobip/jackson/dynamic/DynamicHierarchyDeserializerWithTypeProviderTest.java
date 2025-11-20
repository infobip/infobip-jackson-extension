package com.infobip.jackson.dynamic;

import com.infobip.jackson.TestBase;
import com.infobip.jackson.TypeProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

class DynamicHierarchyDeserializerWithTypeProviderTest extends TestBase {

    @Override
    protected JsonMapper.Builder customize(JsonMapper.Builder builder) {
        DynamicHierarchyModule module = new DynamicHierarchyModule();
        module.addDeserializer(FooBar.class, DynamicHierarchyDeserializer.from(FooBarType.class));
        return builder.addModules(module);
    }

    @Test
    void shouldDeserializeFooAsFooBarFromJson() {
        // given
        String json = "{'type':'FOO','foo':'foo'}";

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
        String json = "{'type':'BAR','bar':'bar'}";

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
        String json = "{'type':'baz'}";

        // when
        Throwable actual = catchThrowable(() -> jsonMapper.readValue(json, FooBar.class));

        // then
        then(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("No java type mapping specified for json value: baz");
    }

    @Test
    void shouldDeserializeFooAsFooFromJson() {
        // given
        String json = "{'type':'FOO','foo':'foo'}";

        // when
        Foo actual = jsonMapper.readValue(json, Foo.class);

        // then
        then(actual).isEqualTo(new Foo("foo"));
    }

    interface FooBar {

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
    }
}
