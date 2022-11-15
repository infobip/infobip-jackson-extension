package com.infobip.jackson;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class CompositeJsonTypeResolver<E extends Enum<E>> implements JsonTypeResolver {

    private final Class<E> type;
    private final Class<?> defaultType;
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
        this.defaultType = Stream.of(type.getEnumConstants())
                                 .findAny()
                                 .flatMap(value -> {
                                     if (value instanceof TypeProvider<?>) {
                                         return ((TypeProvider<?>) value).getDefaultType();
                                     }

                                     return Optional.empty();
                                 })
                                 .orElse(null);
        this.type = requireNonNull(type);
        this.typePropertyName = requireNonNull(typePropertyName);
        this.converter = requireNonNull(converter);
        this.valueInUpperCase = valueInUpperCase;
    }

    @Override
    public Class<?> resolve(Map<String, Object> json) {
        String jsonValueInCorrectCase = getJsonValueInCorrectCase(json);
        Class<?> defaultType = this.defaultType;

        if (Objects.isNull(jsonValueInCorrectCase) && Objects.nonNull(defaultType)) {
            return defaultType;
        }

        return converter.apply(Enum.valueOf(type, jsonValueInCorrectCase));
    }

    private String getJsonValueInCorrectCase(Map<String, Object> json) {
        Object rawJsonValue = json.get(typePropertyName);

        if (Objects.isNull(rawJsonValue)) {
            return null;
        }

        String jsonValue = rawJsonValue.toString();

        if (!valueInUpperCase) {
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
