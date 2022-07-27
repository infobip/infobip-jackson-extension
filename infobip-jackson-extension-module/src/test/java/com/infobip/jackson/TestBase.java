package com.infobip.jackson;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestBase {

    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(ALLOW_SINGLE_QUOTES, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new Jdk8Module());
        this.objectMapper.registerModule(new ParameterNamesModule());
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.registerModule(new InfobipJacksonModule());
    }
}
