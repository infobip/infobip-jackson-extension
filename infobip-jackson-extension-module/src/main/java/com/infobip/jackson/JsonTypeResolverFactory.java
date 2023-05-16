package com.infobip.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JsonTypeResolverFactory {

    private final Set<Class<?>> ignoredClasses = Set.of(SimpleJsonHierarchy.class,
                                                        PresentPropertyJsonHierarchy.class);

    public Optional<JsonTypeResolver> create(Class<?> type) {
        if (Objects.isNull(type)) {
            return Optional.empty();
        }

        if (ignoredClasses.contains(type)) {
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
            if (isSubtypeOf(type, PresentPropertyJsonTypeResolver.class)) {
                return createSubtypeOfPresentPropertyJsonTypeResolver(type, targetType);
            }
            return (JsonTypeResolver) getConstructor(type).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to resolve default constructor for " + type, e);
        }
    }

    private <E extends Enum<E> & TypeProvider<?>> SimpleJsonTypeResolver<E> createSimpleTypeJsonResolver(Class<?> type) {
        Class<E> enumType = resolveFirstGenericTypeArgument(type, SimpleJsonHierarchy.class);
        String propertyName = Optional.ofNullable(extractAnnotation(type, JsonTypePropertyName.class))
                                      .map(JsonTypePropertyName::value)
                                      .orElse(JsonTypePropertyName.DEFAULT_TYPE_PROPERTY_NAME);
        boolean upperCase = Optional.ofNullable(extractAnnotation(type, JsonTypePropertyValue.class))
                                    .map(JsonTypePropertyValue::inUpperCase)
                                    .orElse(JsonTypePropertyValue.DEFAULT_IN_UPPER_CASE);
        return new SimpleJsonTypeResolver<>(enumType, propertyName, upperCase);
    }

    private <E extends Enum<E> & TypeProvider<?>> PresentPropertyJsonTypeResolver<E> createPresentPropertyJsonTypeResolver(
        Class<?> type) {
        Class<E> enumType = resolveFirstGenericTypeArgument(type, PresentPropertyJsonHierarchy.class);
        return new PresentPropertyJsonTypeResolver<>(enumType);
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E> & TypeProvider<?>> PresentPropertyJsonTypeResolver<E> createSubtypeOfPresentPropertyJsonTypeResolver(
        Class<?> resolverType,
        Class<?> targetType) throws
        IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {

        Class<E> enumType = resolveFirstGenericTypeArgument(targetType, PresentPropertyJsonHierarchy.class);
        for (Constructor<?> cctor : resolverType.getConstructors()) {
            if (cctor.getParameterCount() == 0) {
                return (PresentPropertyJsonTypeResolver<E>) getConstructor(resolverType).newInstance();
            } else if (cctor.getParameterCount() == 1 && cctor.getParameterTypes()[0].equals(Class.class)) {
                return (PresentPropertyJsonTypeResolver<E>) getConstructor(resolverType, Class.class).newInstance(
                    enumType);
            }
        }
        throw new IllegalArgumentException("Failed to resolve default constructor for " + resolverType);
    }

    private Constructor<?> getConstructor(Class<?> resolved, Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<?> constructor = resolved.getDeclaredConstructor(parameterTypes);

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
            if (genericInterface instanceof final ParameterizedType parameterizedInterface) {
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
        List<Type> allInterfaces = new ArrayList<>();

        for (Type anInterface : type.getGenericInterfaces()) {
            allInterfaces.add(anInterface);
            if (anInterface instanceof Class<?>) {
                allInterfaces.addAll(getAllInterfaces((Class<?>) anInterface));
            }
        }

        Optional.ofNullable(type.getGenericSuperclass())
                .map(superClass -> superClass instanceof ParameterizedType ?
                        ((ParameterizedType) superClass).getRawType() : superClass)
                .ifPresent(superClass -> allInterfaces.addAll(getAllInterfaces((Class<?>) superClass)));

        return allInterfaces;
    }

    private boolean isSubtypeOf(Class<?> type, Class<?> parentType) {
        return type.getSuperclass() != null
               && (type.getSuperclass().equals(parentType) || isSubtypeOf(type.getSuperclass(), parentType));
    }

}
