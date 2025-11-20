package com.infobip.jackson;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class PresentPropertyCaseFormatDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeRoadBikeAsBikeFromJson() {
        // given
        String json = "{'road_bike':'road bike'}";

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsBikeFromSerializedRoadBike() {
        // given
        String json = jsonMapper.writeValueAsString(new RoadBike("road bike"));

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Test
    void shouldDeserializeMountainBikeAsBikeFromJson() {
        // given
        String json = "{'mountain_bike':'mountain bike'}";

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new MountainBike("mountain bike"));
    }

    @Test
    void shouldDeserializeMountainBikeAsBikeFromSerializedMountainBike() {
        // given
        String json = jsonMapper.writeValueAsString(new MountainBike("mountain bike"));

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new MountainBike("mountain bike"));
    }

    @Test
    void shouldDeserializeListOfBikes() {
        // given
        String json = jsonMapper.writeValueAsString(
                Arrays.asList(new RoadBike("road bike"), new MountainBike("mountain bike")));

        // when
        List<Bike> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new RoadBike("road bike"), new MountainBike("mountain bike")));
    }

    @Test
    void shouldDeserializeBikeWithMultipleMatchingProperties() {
        // given
        String json = "{'mountain_bike':'', 'road_bike': 'road bike'}";

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsRoadBikeFromJson() {
        // given
        String json = "{'road_bike':'road bike'}";

        // when
        RoadBike actual = jsonMapper.readValue(json, RoadBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonTypeResolveWith(LowerUnderscorePresentPropertyJsonTypeResolver.class)
    interface Bike extends PresentPropertyJsonHierarchy<BikeType> {

    }

    static class LowerUnderscorePresentPropertyJsonTypeResolver<E extends Enum<E> & TypeProvider<E>>
            extends PresentPropertyJsonTypeResolver<E> {

        public LowerUnderscorePresentPropertyJsonTypeResolver(Class<E> type) {
            super(type, CaseFormat.LOWER_UNDERSCORE);
        }
    }

    record RoadBike(String roadBike) implements Bike {

    }

    record MountainBike(String mountainBike) implements Bike {

    }

    @Getter
    @AllArgsConstructor
    enum BikeType implements TypeProvider<Bike> {
        ROAD_BIKE(RoadBike.class),
        MOUNTAIN_BIKE(MountainBike.class);

        private final Class<? extends Bike> type;
    }
}
