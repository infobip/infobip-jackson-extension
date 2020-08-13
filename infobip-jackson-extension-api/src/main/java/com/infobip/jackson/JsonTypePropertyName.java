package com.infobip.jackson;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTypePropertyName {

    String DEFAULT_TYPE_PROPERTY_NAME = "type";

    String value();
}
