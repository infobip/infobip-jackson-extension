package com.infobip.jackson.dynamic;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    final Class<T> hierarchyRootType;
    final Map<String, Class<? extends T>> jsonValueToJavaType;
    final String jsonValuePropertyName;

    public DynamicHierarchyDeserializer(Class<T> hierarchyRootType, List<JsonValueToJavaTypeJacksonMapping<T>> mappings) {
        this(hierarchyRootType, mappings, "type");
    }

    public DynamicHierarchyDeserializer(Class<T> hierarchyRootType, List<JsonValueToJavaTypeJacksonMapping<T>> mappings, String jsonValuePropertyName) {
        this.hierarchyRootType = hierarchyRootType;
        this.jsonValueToJavaType = mappings.stream()
                                           .collect(Collectors.toMap(JsonValueToJavaTypeJacksonMapping::getJsonValue,
                                                                     JsonValueToJavaTypeJacksonMapping::getJavaType));
        this.jsonValuePropertyName = jsonValuePropertyName;
    }

    public static  <R, V extends Enum<V> & TypeProvider<R>> DynamicHierarchyDeserializer<R> from(Class<V> enumTypeProvider) {
        return new DynamicHierarchyDeserializer<>(getHierarchyRootType(enumTypeProvider),
                                                  JsonValueToJavaTypeJacksonMapping.from(enumTypeProvider));
    }

    public static  <R, V extends Enum<V> & TypeProvider<R>> DynamicHierarchyDeserializer<R> from(Class<V> enumTypeProvider,
                                                                                                 String jsonValuePropertyName) {
        return new DynamicHierarchyDeserializer<>(getHierarchyRootType(enumTypeProvider),
                                                  JsonValueToJavaTypeJacksonMapping.from(enumTypeProvider), jsonValuePropertyName);
    }

    private static <R, V extends Enum<V> & TypeProvider<R>> Class<R> getHierarchyRootType(Class<V> enumTypeProvider) {

        Type[] genericInterfaces = enumTypeProvider.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedInterface = (ParameterizedType) genericInterface;
                if (TypeProvider.class.equals(parameterizedInterface.getRawType())) {
                    @SuppressWarnings("unchecked")
                    Class<R> typeArgument = (Class<R>) parameterizedInterface.getActualTypeArguments()[0];
                    return typeArgument;
                }
            }
        }

        throw new IllegalArgumentException(
            "Failed to resolve type argument " + enumTypeProvider + " " + TypeProvider.class + " with index" + 0);
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

    public Class<T> getHierarchyRootType() {
        return hierarchyRootType;
    }

}
