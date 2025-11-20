package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.*;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.stream.Stream;

public class SingleArgumentPropertiesCreatorModeAnnotationIntrospector extends NopAnnotationIntrospector {

    @Override
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        if (!(a instanceof AnnotatedConstructor)) {
            return null;
        }

        AnnotatedConstructor annotatedConstructor = (AnnotatedConstructor) a;

        if (annotatedConstructor.getParameterCount() != 1) {
            return null;
        }

        Class<?> declaringClass = annotatedConstructor.getDeclaringClass();

        if (declaringClass.getDeclaredConstructors().length > 1) {
            return null;
        }

        if (doesntHaveMatchingFieldAndParameter(annotatedConstructor, declaringClass)) {
            return null;
        }

        return JsonCreator.Mode.PROPERTIES;
    }

    private boolean doesntHaveMatchingFieldAndParameter(AnnotatedConstructor annotatedConstructor,
                                                        Class<?> declaringClass) {
        Parameter parameter = annotatedConstructor.getAnnotated().getParameters()[0];
        return Stream.of(declaringClass.getDeclaredFields())
                     .noneMatch(field -> doesFieldMatchParameter(field, parameter));
    }

    private boolean doesFieldMatchParameter(Field field, Parameter parameter) {

        if(!field.getType().equals(parameter.getType())) {
            return false;
        }

        String parameterName = parameter.getName();
        JsonProperty annotation = field.getAnnotation(JsonProperty.class);

        if (Objects.nonNull(annotation) && annotation.value().equals(parameterName)) {
            return true;
        }

        return field.getName().equals(parameterName);
    }
}
