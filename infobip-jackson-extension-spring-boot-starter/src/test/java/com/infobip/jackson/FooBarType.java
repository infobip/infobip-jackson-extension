package com.infobip.jackson;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum FooBarType implements TypeProvider<FooBar> {
    FOO(Foo.class);

    private final Class<? extends FooBar> type;
}
