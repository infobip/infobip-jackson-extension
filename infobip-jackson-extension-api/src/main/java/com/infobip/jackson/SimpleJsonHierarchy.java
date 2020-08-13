package com.infobip.jackson;

@JsonTypeResolveWith(SimpleJsonTypeResolver.class)
public interface SimpleJsonHierarchy<E extends Enum<E> & TypeProvider> {

    E getType();
}