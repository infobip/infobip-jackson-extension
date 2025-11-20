package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.*;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

class SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest extends TestBase {

    @Test
    void shouldDeserializeClassWithSingleFieldAndOnlySingleParameterConstructor() throws JsonProcessingException {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        ClassWithSingleFieldAndOnlySingleParameterConstructor actual = objectMapper.readValue(givenJson,
                                                                                              ClassWithSingleFieldAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithSingleFieldAndOnlySingleParameterConstructor("givenFoo"));
    }

    @Test
    void shouldDeserializeClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor() throws JsonProcessingException {
        // given
        String givenJson = "{'bar':'givenBar'}";

        // when
        ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor actual = objectMapper.readValue(givenJson,
                                                                                                                       ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithSingleFieldAnnotatedWithJsonPropertyAndOnlySingleParameterConstructor("givenBar"));
    }

    @Test
    void shouldDeserializeClassWithMultipleProperties() throws JsonProcessingException {
        // given
        String givenJson = "{'foo':'givenFoo', 'bar':'givenBar'}";

        // when
        ClassWithMultipleProperties actual = objectMapper.readValue(givenJson, ClassWithMultipleProperties.class);

        // then
        then(actual).isEqualTo(new ClassWithMultipleProperties("givenFoo", "givenBar"));
    }

    @Test
    void shouldDeserializeClassWithMultipleFieldsAndOnlySingleParameterConstructor() throws
            JsonProcessingException {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        ClassWithMultipleFieldsAndOnlySingleParameterConstructor actual = objectMapper.readValue(givenJson,
                                                                                                 ClassWithMultipleFieldsAndOnlySingleParameterConstructor.class);

        // then
        then(actual).isEqualTo(new ClassWithMultipleFieldsAndOnlySingleParameterConstructor("givenFoo"));
    }

    @Test
    void shouldDeserializeClassWithDelegatingCreatorConstructor() throws
            JsonProcessingException {
        // given
        String givenJson = "{'foo':'givenFoo'}";

        // when
        ClassWithMultipleFieldsAndOnlySingleParameterConstructor actual = objectMapper.readValue(givenJson,
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
                () -> objectMapper.readValue(givenJson, ClassWithMultipleConstructors.class));

        // then
        then(actual).isInstanceOf(MismatchedInputException.class)
                    .hasMessage("Cannot construct instance of `com.infobip.jackson.SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest$ClassWithMultipleConstructors` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)\n" +
                                        " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 2]");
    }

    @Test
    void shouldFailToDeserializeClassWithMismatchingParameterType() {
        // given
        String givenJson = "{'foo':1}";

        // when
        Throwable actual = BDDAssertions.catchThrowable(
                () -> objectMapper.readValue(givenJson, ClassWithMismatchingParameterType.class));

        // then
        then(actual).isInstanceOf(MismatchedInputException.class)
                    .hasMessage("Cannot construct instance of `com.infobip.jackson.SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest$ClassWithMismatchingParameterType` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)\n" +
                                        " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 2]");
    }

    @Test
    void shouldFailToDeserializeClassWithMismatchingParameterName() {
        // given
        String givenJson = "{'foo':'givenFooBar'}";

        // when
        Throwable actual = BDDAssertions.catchThrowable(() -> objectMapper.readValue(givenJson,
                                                                                     ClassWithMismatchingParameterName.class));

        // then
        then(actual).isInstanceOf(MismatchedInputException.class)
                    .hasMessage("Cannot construct instance of `com.infobip.jackson.SingleArgumentPropertiesCreatorModeAnnotationIntrospectorTest$ClassWithMismatchingParameterName` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)\n" +
                                        " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 2]");
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
