package com.infobip.jackson;

import java.util.Map;

public interface JsonTypeResolver {
    Class<?> resolve(Map<String, Object> json);
}