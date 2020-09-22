package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.CaseFormat;
import lombok.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class PresentPropertyResolverWithDefaultConstructorTest extends TestBase {

    @Test
    void shouldUseResolverWithNoArgsConstructor() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new RoadBike("road bike"));

        // when
        Bike actual = objectMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Getter
    @AllArgsConstructor
    enum BikeType implements TypeProvider {
        ROAD_BIKE(RoadBike.class),
        MOUNTAIN_BIKE(MountainBike.class);

        private final Class<? extends Bike> type;
    }

    @JsonTypeResolveWith(BikeTypeResolver.class)
    interface Bike extends PresentPropertyJsonHierarchy<BikeType> {

    }

    static class BikeTypeResolver extends PresentPropertyJsonTypeResolver<BikeType> {

        public BikeTypeResolver() {
            super(BikeType.class, CaseFormat.LOWER_CAMEL);
        }
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class RoadBike implements Bike {

        private final String roadBike;
    }

    @AllArgsConstructor(onConstructor_ = @JsonCreator)
    @Value
    static class MountainBike implements Bike {

        private final String mountainBike;
    }
}
