package com.infobip.jackson.dynamic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobip.jackson.TypeProvider;

public class DynamicHierarchyDeserializer<T> extends JsonDeserializer<T> {

    final Map<String, Class<? extends T>> jsonValueToJavaType;
    final String jsonValuePropertyName;

    public DynamicHierarchyDeserializer(List<JsonValueToJavaTypeJacksonMapping<T>> mappings) {
        this(mappings, "type");
    }

    public DynamicHierarchyDeserializer(List<JsonValueToJavaTypeJacksonMapping<T>> mappings, String jsonValuePropertyName) {
        this.jsonValueToJavaType = mappings.stream()
                                           .collect(Collectors.toMap(JsonValueToJavaTypeJacksonMapping::getJsonValue,
                                                                     JsonValueToJavaTypeJacksonMapping::getJavaType));
        this.jsonValuePropertyName = jsonValuePropertyName;
    }

    public static  <R, V extends Enum<V> & TypeProvider<R>> DynamicHierarchyDeserializer<R> from(Class<V> enumTypeProvider) {
        return new DynamicHierarchyDeserializer<>(JsonValueToJavaTypeJacksonMapping.from(enumTypeProvider));
    }

    public static  <R, V extends Enum<V> & TypeProvider<R>> DynamicHierarchyDeserializer<R> from(Class<V> enumTypeProvider,
                                                                                                 String jsonValuePropertyName) {
        return new DynamicHierarchyDeserializer<>(JsonValueToJavaTypeJacksonMapping.from(enumTypeProvider), jsonValuePropertyName);
    }

    @Override
    public final T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode tree = codec.readTree(p);
        String jsonValue = tree.get(jsonValuePropertyName).textValue();
        Class<? extends T> javaType = jsonValueToJavaType.get(jsonValue);

        if(Objects.isNull(javaType)) {
            throw new IllegalArgumentException("No java type mapping specified for json value: " + jsonValue);
        }

        return ((ObjectMapper) codec).convertValue(tree, javaType);
    }
}
