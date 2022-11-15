package com.infobip.jackson;

import java.util.Optional;

public interface TypeProvider<T> {

    Class<? extends T> getType();

    default Optional<Class<? extends T>> getDefaultType() { return Optional.empty(); }
}
