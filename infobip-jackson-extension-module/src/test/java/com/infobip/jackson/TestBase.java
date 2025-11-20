package com.infobip.jackson;

import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.json.JsonMapper;

import static tools.jackson.core.json.JsonReadFeature.ALLOW_SINGLE_QUOTES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public abstract class TestBase {

    protected JsonMapper jsonMapper;

    @BeforeEach
    public void setUp() {
        var builder = JsonMapper.builder()
                                .configure(ALLOW_SINGLE_QUOTES, true)
                                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .addModules(new InfobipJacksonModule());
        this.jsonMapper = customize(builder).build();
    }

    protected JsonMapper.Builder customize(JsonMapper.Builder builder) {
        return builder;
    }
}
