package com.infobip.jackson;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTypeResolveWith {

    Class<? extends JsonTypeResolver> value();
}
