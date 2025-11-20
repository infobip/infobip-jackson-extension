package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.MismatchedInputException;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

class SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest extends TestBase {

    @Test
    void shouldDeserializeClassWithSingleFieldAndOnlySingleParameterConstructor() {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        ClassWithSingleFieldAndOnlySingleParameterConstructor actual = jsonMapper.readValue(givenJson,
                                                                                            ClassWithSingleFieldAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithSingleFieldAndOnlySingleParameterConstructor("givenFoo"));
    }

    @Test
    void shouldDeserializeClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor() {
        // given
        String givenJson = "{'bar':'givenBar'}";

        // when
        ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor actual = jsonMapper.readValue(givenJson,
                                                                                                                     ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor("givenBar"));
    }

    @Test
    void shouldDeserializeClassWithMultipleProperties() {
        // given
        String givenJson = "{'foo':'givenFoo', 'bar':'givenBar'}";

        // when
        ClassWithMultipleProperties actual = jsonMapper.readValue(givenJson, ClassWithMultipleProperties.class);

        // then
        then(actual).isEqualTo(new ClassWithMultipleProperties("givenFoo", "givenBar"));
    }

    @Test
    void shouldDeserializeClassWithMultipleFieldsAndOnlySingleParameterConstructor() {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        ClassWithMultipleFieldsAndOnlySingleParameterConstructor actual = jsonMapper.readValue(givenJson,
                                                                                               ClassWithMultipleFieldsAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithMultipleFieldsAndOnlySingleParameterConstructor("givenFoo"));
    }

    @Test
    void shouldDeserializeClassWithDelegatingCreatorConstructor() {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        ClassWithMultipleFieldsAndOnlySingleParameterConstructor actual = jsonMapper.readValue(givenJson,
                                                                                               ClassWithMultipleFieldsAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithMultipleFieldsAndOnlySingleParameterConstructor("givenFoo"));
    }

    @Test
    void shouldFailToDeserializeClassWithMultipleConstructors() {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        Throwable actual = BDDAssertions.catchThrowable(
                () -> jsonMapper.readValue(givenJson, ClassWithMultipleConstructors.class));

        // then
        then(actual).isInstanceOf(MismatchedInputException.class)
                    .hasMessage("Cannot construct instance of `com.infobip.jackson.SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest$ClassWithMultipleConstructors` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)\n" +
                                        " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]");
    }

    @Test
    void shouldFailToDeserializeClassWithMismatchingParameterType() {
        // given
        String givenJson = "{'foo':1}";

        // when
        Throwable actual = BDDAssertions.catchThrowable(
                () -> jsonMapper.readValue(givenJson, ClassWithMismatchingParameterType.class));

        // then
        then(actual).isInstanceOf(MismatchedInputException.class)
                    .hasMessage("Cannot construct instance of `com.infobip.jackson.SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest$ClassWithMismatchingParameterType` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)\n" +
                                        " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]");
    }

    @Test
    void shouldFailToDeserializeClassWithMismatchingParameterName() {
        // given
        String givenJson = "{'foo':'givenFooBar'}";

        // when
        Throwable actual = BDDAssertions.catchThrowable(() -> jsonMapper.readValue(givenJson,
                                                                                   ClassWithMismatchingParameterName.class));

        // then
        then(actual).isInstanceOf(MismatchedInputException.class)
                    .hasMessage("Cannot construct instance of `com.infobip.jackson.SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest$ClassWithMismatchingParameterName` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)\n" +
                                        " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]");
    }

    @Value
    static class ClassWithSingleFieldAndOnlySingleParameterConstructor {

        private final String foo;
    }

    @Value
    static class ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor {

        @JsonProperty("bar")
        private final String foo;

        public ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor(String bar) {
            this.foo = bar;
        }
    }

    @Value
    static class ClassWithMultipleProperties {

        private final String foo;
        private final String bar;
    }

    @Value
    static class ClassWithMultipleFieldsAndOnlySingleParameterConstructor {

        private final String foo;
        private final String bar = "bar";
    }

    @Value
    static class ClassWithDelegatingCreatorConstructor {

        private final String foo;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public ClassWithDelegatingCreatorConstructor(Map<String, String> foo) {
            this.foo = foo.get("foo");
        }
    }

    @ToString
    @EqualsAndHashCode
    static class ClassWithMultipleConstructors {

        private final String foo;

        public ClassWithMultipleConstructors(String foo) {
            this.foo = foo;
        }

        public ClassWithMultipleConstructors(Integer bar) {
            this.foo = null;
        }
    }

    static class ClassWithMismatchingParameterType {

        private final String foo;

        public ClassWithMismatchingParameterType(Integer foo) {
            this.foo = foo.toString();
        }
    }

    static class ClassWithMismatchingParameterName {

        private final String foo;

        public ClassWithMismatchingParameterName(String bar) {
            this.foo = bar;
        }
    }
}
