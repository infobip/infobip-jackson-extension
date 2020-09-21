package com.infobip.jackson;

import com.google.common.base.CaseFormat;

import java.util.Map;
import java.util.Objects;

public class PresentPropertyJsonTypeResolver<E extends Enum<E> & TypeProvider> implements JsonTypeResolver {

    private final Class<E> type;
    private final CaseFormat caseFormat;

    public PresentPropertyJsonTypeResolver(Class<E> type) {
        this(type, CaseFormat.LOWER_CAMEL);
    }

    protected PresentPropertyJsonTypeResolver(Class<E> type, CaseFormat caseFormat) {
        this.type = Objects.requireNonNull(type);
        this.caseFormat = Objects.requireNonNull(caseFormat);
    }

    @Override
    public final Class<?> resolve(Map<String, Object> json) {
        for (E constant : type.getEnumConstants()) {
            if (json.containsKey(CaseFormat.UPPER_UNDERSCORE.to(caseFormat, constant.toString()))) {
                return constant.getType();
            }
        }

        throw new IllegalArgumentException("Failed to resolve type " + json);
    }
}