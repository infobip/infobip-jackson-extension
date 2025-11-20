package com.infobip.jackson.dynamic;

import com.infobip.jackson.TypeProvider;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.node.ObjectNode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicHierarchyDeserializer<T> extends ValueDeserializer<T> {

    private final Class<T> hierarchyRootType;
    private final Map<String, Class<? extends T>> jsonValueToJavaType;
    private final String jsonValuePropertyName;

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
    public final T deserialize(JsonParser p, DeserializationContext ctxt) {
        var codec = p.objectReadContext();
        JsonNode tree = codec.readTree(p);
        String jsonValue = tree.get(jsonValuePropertyName).textValue();
        Class<? extends T> javaType = jsonValueToJavaType.get(jsonValue);

        if(Objects.isNull(javaType)) {
            throw new IllegalArgumentException("No java type mapping specified for json value: " + jsonValue);
        }

        return ctxt.readTreeAsValue(tree, javaType);
    }

    public Class<T> getHierarchyRootType() {
        return hierarchyRootType;
    }

    public Map<String, Class<? extends T>> getJsonValueToJavaType() {
        return jsonValueToJavaType;
    }

    public String getJsonValuePropertyName() {
        return jsonValuePropertyName;
    }

}
