package com.infobip.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;

class JsonTypedDeserializer<T> extends  ValueDeserializer<T> {

    private final JsonTypeResolver resolver;

    public JsonTypedDeserializer(JsonTypeResolver resolver) {
        this.resolver = resolver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) {
        var b = p.readValueAs(ObjectNode.class);
        var object = ctxt.readTreeAsValue(b, Map.class);
        var resolvedType = resolver.resolve(object);
        return (T) ctxt.readTreeAsValue(b, resolvedType);
    }
}