package com.infobip.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

@Deprecated(forRemoval = true)
public class LegacyInfobipJacksonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.insertAnnotationIntrospector(new LegacyInfobipJacksonAnnotationIntrospector());
        context.insertAnnotationIntrospector(new LegacySingleArgumentPropertiesCreatorModeAnnotationIntrospector());
    }
}
