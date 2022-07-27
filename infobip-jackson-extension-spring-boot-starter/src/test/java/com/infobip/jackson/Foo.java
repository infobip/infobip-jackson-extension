package com.infobip.jackson;

import lombok.Value;

@Value
public class Foo implements FooBar {

    private final String foo;

    @Override
    public FooBarType getType() {
        return FooBarType.FOO;
    }

}
