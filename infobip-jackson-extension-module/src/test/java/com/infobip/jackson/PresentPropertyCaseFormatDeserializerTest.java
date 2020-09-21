package com.infobip.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.CaseFormat;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class PresentPropertyCaseFormatDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeRoadBikeAsBikeFromJson() throws JsonProcessingException {
        // given
        String json = "{'road_bike':'road bike'}";

        // when
        Bike actual = objectMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsBikeFromSerializedRoadBike() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new RoadBike("road bike"));

        // when
        Bike actual = objectMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Test
    void shouldDeserializeMountainBikeAsBikeFromJson() throws JsonProcessingException {
        // given
        String json = "{'mountain_bike':'mountain bike'}";

        // when
        Bike actual = objectMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new MountainBike("mountain bike"));
    }

    @Test
    void shouldDeserializeMountainBikeAsBikeFromSerializedMountainBike() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new MountainBike("mountain bike"));

        // when
        Bike actual = objectMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new MountainBike("mountain bike"));
    }

    @Test
    void shouldDeserializeListOfBikes() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(
                Arrays.asList(new RoadBike("road bike"), new MountainBike("mountain bike")));

        // when
        List<Bike> actual = objectMapper.readValue(json, new TypeReference<List<Bike>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new RoadBike("road bike"), new MountainBike("mountain bike")));
    }

    @Test
    void shouldDeserializeBikeWithMultipleMatchingProperties() throws JsonProcessingException {
        // given
        String json = "{'mountain_bike':'', 'road_bike': 'road bike'}";

        // when
        Bike actual = objectMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsRoadBikeFromJson() throws JsonProcessingException {
        // given
        String json = "{'road_bike':'road bike'}";

        // when
        RoadBike actual = objectMapper.readValue(json, RoadBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("road bike"));
    }

    @JsonTypeResolveWith(LowerUnderscorePresentPropertyJsonTypeResolver.class)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    interface Bike extends PresentPropertyJsonHierarchy<BikeType> {

    }

    static class LowerUnderscorePresentPropertyJsonTypeResolver<E extends Enum<E> & TypeProvider>
            extends PresentPropertyJsonTypeResolver<E> {

        public LowerUnderscorePresentPropertyJsonTypeResolver(Class<E> type) {
            super(type, CaseFormat.LOWER_UNDERSCORE);
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

    @Getter
    @AllArgsConstructor
    enum BikeType implements TypeProvider {
        ROAD_BIKE(RoadBike.class),
        MOUNTAIN_BIKE(MountainBike.class);

        private final Class<? extends Bike> type;
    }
}
