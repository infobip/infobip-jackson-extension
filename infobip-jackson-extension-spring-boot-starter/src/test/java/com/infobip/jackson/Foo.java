package com.infobip.jackson;

public record Foo(String foo) implements FooBar {

    @Override
    public FooBarType getType() {
        return FooBarType.FOO;
    }

}
