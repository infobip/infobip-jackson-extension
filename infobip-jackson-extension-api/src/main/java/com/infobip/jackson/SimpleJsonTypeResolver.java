package com.infobip.jackson;

import static com.infobip.jackson.JsonTypePropertyName.DEFAULT_TYPE_PROPERTY_NAME;

public class SimpleJsonTypeResolver<E extends Enum<E> & TypeProvider<?>> extends CompositeJsonTypeResolver<E> {

    public SimpleJsonTypeResolver(Class<E> type) {
        this(type, DEFAULT_TYPE_PROPERTY_NAME);
    }

    public SimpleJsonTypeResolver(Class<E> type, String typePropertyName) {
        super(type, typePropertyName, e -> e.getType());
    }

    public SimpleJsonTypeResolver(Class<E> type, String typePropertyName, boolean valueInUpperCase) {
        super(type, typePropertyName, e -> e.getType(), valueInUpperCase);
    }
}