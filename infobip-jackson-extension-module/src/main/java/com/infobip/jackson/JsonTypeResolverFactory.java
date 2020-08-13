package com.infobip.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class JsonTypeResolverFactory {

    public Optional<JsonTypeResolver> create(Class<?> type) {
        if (Objects.isNull(type)) {
            return Optional.empty();
        }

        return Optional.ofNullable(extractAnnotation(type, JsonTypeResolveWith.class))
                       .map(annotation -> createJsonTypeResolver(type, annotation.value()));
    }

    private JsonTypeResolver createJsonTypeResolver(Class<?> targetType,
                                                    Class<? extends JsonTypeResolver> type) {
        if (type.equals(SimpleJsonTypeResolver.class)) {
            return createSimpleTypeJsonResolver(targetType);
        }

        if (type.equals(PresentPropertyJsonTypeResolver.class)) {
            return createPresentPropertyJsonTypeResolver(targetType);
        }

        try {
            return (JsonTypeResolver) getConstructor(type).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to resolve default constructor for " + type, e);
        }
    }

    private <E extends Enum<E> & TypeProvider> SimpleJsonTypeResolver<E> createSimpleTypeJsonResolver(Class<?> type) {
        Class<E> enumType = resolveFirstGenericTypeArgument(type, SimpleJsonHierarchy.class);
        String propertyName = Optional.ofNullable(extractAnnotation(type, JsonTypePropertyName.class))
                                   .map(JsonTypePropertyName::value)
                                   .orElse(JsonTypePropertyName.DEFAULT_TYPE_PROPERTY_NAME);
        boolean upperCase = Optional.ofNullable(extractAnnotation(type, JsonTypePropertyValue.class))
                                .map(JsonTypePropertyValue::inUpperCase)
                                .orElse(JsonTypePropertyValue.DEFAULT_IN_UPPER_CASE);
        return new SimpleJsonTypeResolver<>(enumType, propertyName, upperCase);
    }

    private <E extends Enum<E> & TypeProvider> PresentPropertyJsonTypeResolver<E> createPresentPropertyJsonTypeResolver(
            Class<?> type) {
        Class<E> enumType = resolveFirstGenericTypeArgument(type, PresentPropertyJsonHierarchy.class);
        return new PresentPropertyJsonTypeResolver<>(enumType);
    }

    private Constructor<?> getConstructor(Class<?> resolved) throws NoSuchMethodException {
        Constructor<?> constructor = resolved.getDeclaredConstructor();

        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        return constructor;
    }

    private <A extends Annotation> A extractAnnotation(Class<?> type, Class<A> annotationType) {
        if (Objects.isNull(type)) {
            return null;
        }

        A currentJsonTypeResolveWith = type.getAnnotation(annotationType);

        if (Objects.nonNull(currentJsonTypeResolveWith)) {
            return currentJsonTypeResolveWith;
        }

        Class<?>[] interfaces = type.getInterfaces();

        for (Class<?> anInterface : interfaces) {
            A annotationJsonTypeResolveWith = extractAnnotation(anInterface, annotationType);
            if (Objects.nonNull(annotationJsonTypeResolveWith)) {
                return annotationJsonTypeResolveWith;
            }
        }

        return extractAnnotation(type.getSuperclass(), annotationType);
    }

    private <T> T resolveFirstGenericTypeArgument(Class<?> type, Class<?> interfaceType) {
        List<Type> genericInterfaces = getAllInterfaces(type);
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedInterface = (ParameterizedType) genericInterface;
                if (interfaceType.equals(parameterizedInterface.getRawType())) {
                    @SuppressWarnings("unchecked")
                    T typeArgument = (T) parameterizedInterface.getActualTypeArguments()[0];
                    return typeArgument;
                }
            }
        }

        throw new IllegalArgumentException(
                "Failed to resolve type argument " + type + " " + interfaceType + " with index" + 0);
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
