package com.infobip.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.infobip.jackson.InfobipJacksonModule;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;

abstract class TestBase {

    protected final ObjectMapper objectMapper;

    public TestBase() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(ALLOW_SINGLE_QUOTES, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new Jdk8Module());
        this.objectMapper.registerModule(new ParameterNamesModule());
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.registerModule(new InfobipJacksonModule());
    }
}
