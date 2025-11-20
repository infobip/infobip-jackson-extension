package com.infobip.jackson;

import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;

import java.lang.reflect.Modifier;

public class InfobipJacksonAnnotationIntrospector extends NopAnnotationIntrospector {

    private final JsonTypeResolverFactory factory = new JsonTypeResolverFactory();

    @Override
    public Object findDeserializer(MapperConfig<?> config, Annotated am) {
        Class<?> rawType = am.getRawType();

        if(!Modifier.isAbstract(rawType.getModifiers())) {
            return super.findDeserializer(config, am);
        }

        return factory.create(rawType)
                      .map(resolver -> (Object) new JsonTypedDeserializer<>(resolver))
                      .orElseGet(() -> super.findDeserializer(config, am));
    }
}
