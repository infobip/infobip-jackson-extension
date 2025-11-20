package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

public class PresentPropertyResolverWithDefaultConstructorTest extends TestBase {

    @Test
    void shouldUseResolverWithNoArgsConstructor() {
        // given
        String json = jsonMapper.writeValueAsString(new RoadBike("road bike"));

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

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

    record RoadBike(String roadBike) implements Bike {

    }

    record MountainBike(String mountainBike) implements Bike {

    }
}
