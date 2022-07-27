package com.infobip.jackson.dynamic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.infobip.jackson.TypeProvider;

public class JsonValueToJavaTypeJacksonMapping<T> {

    private final String jsonValue;
    private final Class<? extends T> javaType;

    public JsonValueToJavaTypeJacksonMapping(String jsonValue, Class<? extends T> javaType) {
        this.jsonValue = jsonValue;
        this.javaType = javaType;
    }

    public JsonValueToJavaTypeJacksonMapping(Enum<?> enumValue, Class<? extends T> javaType) {
        this(enumValue.toString(), javaType);
    }

    public static  <R, V extends Enum<V> & TypeProvider<R>> List<JsonValueToJavaTypeJacksonMapping<R>> from(Class<V> enumTypeProvider) {
        V[] enumConstants = enumTypeProvider.getEnumConstants();
        return Stream.of(enumConstants)
                     .map(value -> new JsonValueToJavaTypeJacksonMapping<>(value.name(), (Class<R>) value.getType()))
                     .collect(Collectors.toList());
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public Class<? extends T> getJavaType() {
        return javaType;
    }

}
