package com.infobip.jackson;

public interface TypeProvider<T> {

    Class<? extends T> getType();
}
