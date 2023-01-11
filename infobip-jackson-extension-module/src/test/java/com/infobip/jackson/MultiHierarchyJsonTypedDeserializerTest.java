package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
class MultiHierarchyJsonTypedDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeHumanAsAnimalFromJson() throws JsonProcessingException {
        // given
        String json = "{'animalType':'MAMMAL','mammalType':'HUMAN','name':'givenName'}";

        // when
        Animal actual = objectMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeHumanAsAnimalFromSerializedHuman() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Human("givenName"));

        // when
        Animal actual = objectMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeHumanAsMammalFromJson() throws JsonProcessingException {
        // given
        String json = "{'mammalType':'HUMAN','name':'givenName'}";

        // when
        Mammal actual = objectMapper.readValue(json, Mammal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeHumanAsMammalFromSerializedHuman() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Human("givenName"));

        // when
        Mammal actual = objectMapper.readValue(json, Mammal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeListOfAnimals() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(Arrays.asList(new Human("givenName")));

        // when
        List<Animal> actual = objectMapper.readValue(json, new TypeReference<List<Animal>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new Human("givenName")));
    }

    @Test
    void shouldDeserializeListOfMammals() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(Arrays.asList(new Human("givenName")));

        // when
        List<Mammal> actual = objectMapper.readValue(json, new TypeReference<List<Mammal>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new Human("givenName")));
    }

    @Test
    void shouldDeserializeHumanAsHumanFromJson() throws JsonProcessingException {
        // given
        String json = "{'animalType':'MAMMAL','mammalType':'HUMAN','name':'givenName'}";

        // when
        Human actual = objectMapper.readValue(json, Human.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @JsonTypeResolveWith(AnimalJsonTypeResolver.class)
    interface Animal {

        AnimalType getAnimalType();

    }

    static class AnimalJsonTypeResolver extends SimpleJsonTypeResolver<AnimalType> {

        public AnimalJsonTypeResolver() {
            super(AnimalType.class, "animalType");
        }

    }

    @JsonTypeResolveWith(MammalJsonTypeResolver.class)
    interface Mammal extends Animal {

        MammalType getMammalType();

    }

    static class MammalJsonTypeResolver extends SimpleJsonTypeResolver<MammalType> {

        public MammalJsonTypeResolver() {
            super(MammalType.class, "mammalType");
        }

    }

    record Human(String name) implements Mammal {

        @Override
        public AnimalType getAnimalType() {
            return AnimalType.MAMMAL;
        }

        @Override
        public MammalType getMammalType() {
            return MammalType.HUMAN;
        }

    }

    @Getter
    @AllArgsConstructor
    enum AnimalType implements TypeProvider<Animal> {
        MAMMAL(Mammal.class);

        private final Class<? extends Animal> type;
    }

    @Getter
    @AllArgsConstructor
    enum MammalType implements TypeProvider<Mammal> {
        HUMAN(Human.class);

        private final Class<? extends Mammal> type;
    }

}
