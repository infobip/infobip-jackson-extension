package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

@AllArgsConstructor
class PresentPropertyDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeRoadBikeAsBikeFromJson() {
        // given
        String json = "{'roadBike':'roadBike'}";

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeRoadBikeAsBikeFromSerializedRoadBike() {
        // given
        String json = jsonMapper.writeValueAsString(new RoadBike("roadBike"));

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new RoadBike("roadBike"));
    }

    @Test
    void shouldDeserializeBmxAsBikeFromJson() {
        // given
        String json = "{'bmx':'bmx'}";

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new Bmx("bmx"));
    }

    @Test
    void shouldDeserializeBmxAsBikeFromSerializedBmx() {
        // given
        String json = jsonMapper.writeValueAsString(new Bmx("bmx"));

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

        // then
        then(actual).isEqualTo(new Bmx("bmx"));
    }

    @Test
    void shouldDeserializeListOfBikes() {
        // given
        String json = jsonMapper.writeValueAsString(Arrays.asList(new RoadBike("roadBike"), new Bmx("bmx")));

        // when
        List<Bike> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new RoadBike("roadBike"), new Bmx("bmx")));
    }

    @Test
    void shouldDeserializeBikeWithMultipleMatchingProperties() {
        // given
        String json = "{'bmx':'', 'roadBike': 'roadBike'}";

        // when
        Bike actual = jsonMapper.readValue(json, Bike.class);

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

    interface Bike extends PresentPropertyJsonHierarchy<BikeType> {

    }

    record RoadBike(String roadBike) implements Bike {

    }

    record Bmx(String bmx) implements Bike {

    }

    @Getter
    @AllArgsConstructor
    enum BikeType implements TypeProvider<Bike> {
        ROAD_BIKE(RoadBike.class),
        BMX(Bmx.class);

        private final Class<? extends Bike> type;
    }
}
