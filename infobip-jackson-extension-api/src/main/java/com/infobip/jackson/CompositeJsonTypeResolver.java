package com.infobip.jackson;

import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class CompositeJsonTypeResolver<E extends Enum<E>> implements JsonTypeResolver {

    private final Class<E> type;
    private final String typePropertyName;
    private final Function<E, Class<?>> converter;
    private final boolean valueInUpperCase;

    public CompositeJsonTypeResolver(Class<E> type,
                                     String typePropertyName,
                                     Function<E, Class<?>> converter) {
        this(type, typePropertyName, converter, true);
    }

    public CompositeJsonTypeResolver(Class<E> type,
                                     String typePropertyName,
                                     Function<E, Class<?>> converter,
                                     boolean valueInUpperCase) {
        this.type = requireNonNull(type);
        this.typePropertyName = requireNonNull(typePropertyName);
        this.converter = requireNonNull(converter);
        this.valueInUpperCase = valueInUpperCase;
    }

    @Override
    public Class<?> resolve(Map<String, Object> json) {
        return converter.apply(Enum.valueOf(type, getJsonValueInCorrectCase(json)));
    }

    private String getJsonValueInCorrectCase(Map<String, Object> json) {
        String jsonValue = json.get(typePropertyName).toString();

        if(!valueInUpperCase) {
            return jsonValue.toUpperCase();
        }

        return jsonValue;
    }

    public Class<E> getType() {
        return type;
    }

    public String getTypePropertyName() {
        return typePropertyName;
    }

    public Function<E, Class<?>> getConverter() {
        return converter;
    }
}