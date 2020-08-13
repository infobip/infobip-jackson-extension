package com.infobip.jackson;

import java.util.Map;
import java.util.Objects;

public class PresentPropertyJsonTypeResolver<E extends Enum<E> & TypeProvider> implements JsonTypeResolver {

    private final Class<E> type;

    public PresentPropertyJsonTypeResolver(Class<E> type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public final Class<?> resolve(Map<String, Object> json) {
        for (E constant : type.getEnumConstants()) {
            if (json.containsKey(constant.toString().toLowerCase())) {
                return constant.getType();
            }
        }

        throw new IllegalArgumentException("Failed to resolve type " + json);
    }
}