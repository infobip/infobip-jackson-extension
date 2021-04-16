package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.*;

import java.lang.reflect.Field;
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

        String parameterName = annotatedConstructor.getAnnotated().getParameters()[0].getName();
        boolean hasAMatchingField = Stream.of(declaringClass.getDeclaredFields())
                                          .map(Field::getName)
                                          .anyMatch(fieldName -> fieldName.equals(parameterName));
        if(!hasAMatchingField) {
            return null;
        }

        return JsonCreator.Mode.PROPERTIES;
    }
}
