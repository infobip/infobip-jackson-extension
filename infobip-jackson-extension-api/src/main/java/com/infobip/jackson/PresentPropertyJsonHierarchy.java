package com.infobip.jackson;

@JsonTypeResolveWith(PresentPropertyJsonTypeResolver.class)
public interface PresentPropertyJsonHierarchy<E extends Enum<E> & TypeProvider<?>> {
}