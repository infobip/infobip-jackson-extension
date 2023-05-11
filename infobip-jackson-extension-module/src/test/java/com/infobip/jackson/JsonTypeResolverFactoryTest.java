package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class JsonTypeResolverFactoryTest {

    private final JsonTypeResolverFactory factory = new JsonTypeResolverFactory();

    @Test
    void shouldHandleSimpleJsonHierarchy() {

        // when
        var actual = factory.create(SimpleJsonHierarchy.class);

        // then
        then(actual).isEmpty();
    }

    @Test
    void shouldHandlePresentPropertyJsonHierarchyHierarchy() {

        // when
        var actual = factory.create(PresentPropertyJsonHierarchy.class);

        // then
        then(actual).isEmpty();
    }
}