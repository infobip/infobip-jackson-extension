package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class JsonTypeResolverFactoryTest {

    private JsonTypeResolverFactory factory = new JsonTypeResolverFactory();

    @Test
    void shouldHandleSimpleJsonHierarchy() {

        // when
        Optional<JsonTypeResolver> actual = factory.create(SimpleJsonHierarchy.class);

        // then
        then(actual).isEmpty();
    }

}