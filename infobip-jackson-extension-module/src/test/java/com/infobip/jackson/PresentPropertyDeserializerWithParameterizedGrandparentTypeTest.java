package com.infobip.jackson;

import lombok.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class PresentPropertyDeserializerWithParameterizedGrandparentTypeTest extends TestBase {

    @Test
    void shouldDeserializeRoadBikeAsBikeFromJson() {
        // given
        String json = "{'roadBike':'roadBike'}";

        // when
        ParentBike actual = jsonMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsBikeFromSerializedRoadBike() {
        // given
        String json = jsonMapper.writeValueAsString(new RoadBike("roadBike"));

        // when
        ParentBike actual = jsonMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeBmxAsBikeFromJson() {
        // given
        String json = "{'bmx':'bmx'}";

        // when
        ParentBike actual = jsonMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new Bmx("bmx"));
    }

    @Test
    void shouldDeserializeBmxAsBikeFromSerializedBmx() {
        // given
        String json = jsonMapper.writeValueAsString(new Bmx("bmx"));

        // when
        ParentBike actual = jsonMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new Bmx("bmx"));
    }

    @Test
    void shouldDeserializeListOfBikes() {
        // given
        String json = jsonMapper.writeValueAsString(Arrays.asList(new RoadBike("roadBike"), new Bmx("bmx")));

        // when
        List<ParentBike> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new RoadBike("roadBike"), new Bmx("bmx")));
    }

    @Test
    void shouldDeserializeBikeWithMultipleMatchingProperties() {
        // given
        String json = "{'bmx':'', 'roadBike': 'roadBike'}";

        // when
        ParentBike actual = jsonMapper.readValue(json, ParentBike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsRoadBikeFromJson() {
        // given
        String json = "{'roadBike':'roadBike'}";

        // when
        RoadBike actual = jsonMapper.readValue(json, RoadBike.class);

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
