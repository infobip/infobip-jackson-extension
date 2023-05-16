package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.junit.jupiter.api.Test;

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
    enum BikeType implements TypeProvider<Bike> {
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

    @Value
    static class RoadBike implements Bike {

        private final String roadBike;
    }

    @Value
    static class MountainBike implements Bike {

        private final String mountainBike;
    }
}
