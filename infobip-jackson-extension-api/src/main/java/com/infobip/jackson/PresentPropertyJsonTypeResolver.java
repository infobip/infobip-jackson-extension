package com.infobip.jackson;

import java.lang.reflect.Type;
import java.util.*;

public class PresentPropertyJsonTypeResolver<E extends Enum<E> & TypeProvider> implements JsonTypeResolver {

    private final Class<E> type;

    public PresentPropertyJsonTypeResolver(Class<E> type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public final Class<?> resolve(Map<String, Object> json) {
        for (E constant : type.getEnumConstants()) {
            if (json.containsKey(resolvePropertyName(constant))) {
                return constant.getType();
            }
        }

        throw new IllegalArgumentException("Failed to resolve type " + json);
    }

    private String resolvePropertyName(E constant) {
        for (Type type : getAllInterfaces(constant.getClass())) {
            if (type.equals(NamedPropertyTypeProvider.class)) {
                return ((NamedPropertyTypeProvider) constant).getJsonPropertyName();
            }
        }
        return constant.toString().toLowerCase();
    }

    private List<Type> getAllInterfaces(Class<?> type) {
        List<Type> allInterfaces = new ArrayList<Type>();

        for (Type anInterface : type.getGenericInterfaces()) {
            allInterfaces.add(anInterface);
            if (anInterface instanceof Class<?>) {
                allInterfaces.addAll(getAllInterfaces((Class<?>) anInterface));
            }
        }

        return allInterfaces;
    }
}