package com.infobip.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.Map;

class JsonTypedDeserializer<T> extends JsonDeserializer<T> {

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {
    };

    private final JsonTypeResolver resolver;

    public JsonTypedDeserializer(JsonTypeResolver resolver) {
        this.resolver = resolver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, Object> object = p.readValueAs(MAP_TYPE_REFERENCE);
        return (T) ((ObjectMapper) p.getCodec()).convertValue(object, resolver.resolve(object));
    }
}