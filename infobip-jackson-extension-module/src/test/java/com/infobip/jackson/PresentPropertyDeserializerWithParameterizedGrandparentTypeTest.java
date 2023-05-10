package com.infobip.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class PresentPropertyDeserializerWithParameterizedGrandparentTypeTest extends TestBase {

    @Test
    void shouldDeserializeRoadBikeAsBikeFromJson() throws JsonProcessingException {
        // given
        String json = "{'roadBike':'roadBike'}";

        // when
        ParentBike actual = objectMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsBikeFromSerializedRoadBike() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new RoadBike("roadBike"));

        // when
        ParentBike actual = objectMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeBmxAsBikeFromJson() throws JsonProcessingException {
        // given
        String json = "{'bmx':'bmx'}";

        // when
        ParentBike actual = objectMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new Bmx("bmx"));
    }

    @Test
    void shouldDeserializeBmxAsBikeFromSerializedBmx() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Bmx("bmx"));

        // when
        ParentBike actual = objectMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new Bmx("bmx"));
    }

    @Test
    void shouldDeserializeListOfBikes() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(Arrays.asList(new RoadBike("roadBike"), new Bmx("bmx")));

        // when
        List<ParentBike> actual = objectMapper.readValue(json, new TypeReference<List<ParentBike>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new RoadBike("roadBike"), new Bmx("bmx")));
    }

    @Test
    void shouldDeserializeBikeWithMultipleMatchingProperties() throws JsonProcessingException {
        // given
        String json = "{'bmx':'', 'roadBike': 'roadBike'}";

        // when
        ParentBike actual = objectMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsRoadBikeFromJson() throws JsonProcessingException {
        // given
        String json = "{'roadBike':'roadBike'}";

        // when
        RoadBike actual = objectMapper.readValue(json, RoadBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    static abstract class GrandparentBike<ParameterizedType> {

        abstract ParameterizedType getGrandparentStuff();

    }

    static abstract class ParentBike extends GrandparentBike<String> implements PresentPropertyJsonHierarchy<BikeType> {

        @Override
        String getGrandparentStuff() {
            return "foobar";
        }
    }

    @AllArgsConstructor
    @Data
    static class RoadBike extends ParentBike {
        private String roadBike;

    }

    @AllArgsConstructor
    @Data
    static class Bmx extends ParentBike {
        private String bmx;
    }

    @Getter
    @AllArgsConstructor
    enum BikeType implements TypeProvider<ParentBike> {
        ROAD_BIKE(RoadBike.class),
        BMX(Bmx.class);

        private final Class<? extends ParentBike> type;
    }
}
