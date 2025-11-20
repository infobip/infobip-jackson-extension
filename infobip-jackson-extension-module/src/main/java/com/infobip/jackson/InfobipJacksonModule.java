package com.infobip.jackson;

import tools.jackson.databind.module.SimpleModule;

public class InfobipJacksonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.insertAnnotationIntrospector(new InfobipJacksonAnnotationIntrospector());
        context.insertAnnotationIntrospector(new SingleArgumentPropertiesCreatorModeAnnotationIntrospector());
    }
}
