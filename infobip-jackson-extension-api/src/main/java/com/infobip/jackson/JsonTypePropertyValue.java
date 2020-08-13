package com.infobip.jackson;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTypePropertyValue {

    boolean DEFAULT_IN_UPPER_CASE = true;

    boolean inUpperCase();
}
