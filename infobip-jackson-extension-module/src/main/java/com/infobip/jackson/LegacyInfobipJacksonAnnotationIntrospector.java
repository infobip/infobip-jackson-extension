package com.infobip.jackson;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;

import java.lang.reflect.Modifier;

@Deprecated(forRemoval = true)
public class LegacyInfobipJacksonAnnotationIntrospector extends NopAnnotationIntrospector {

    private final JsonTypeResolverFactory factory = new JsonTypeResolverFactory();

    @Override
    public Object findDeserializer(Annotated am) {
        Class<?> rawType = am.getRawType();

        if(!Modifier.isAbstract(rawType.getModifiers())) {
            return super.findDeserializer(am);
        }

        return factory.create(rawType)
                      .map(resolver -> (Object) new LegacyJsonTypedDeserializer<>(resolver))
                      .orElseGet(() -> super.findDeserializer(am));
    }
}
