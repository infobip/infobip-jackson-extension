package com.infobip.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class MultiClassHierarchyWithOneTypeFieldAndMultipleEnumsJsonTypedDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeHumanAsAnimalFromJson() throws JsonProcessingException {
        // given
        String json = "{'type':'HUMAN','name':'givenName'}";

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
        String json = "{'type':'HUMAN','name':'givenName'}";

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
    void shouldDeserializeParrotAsAnimalFromJson() throws JsonProcessingException {
        // given
        String json = "{'type':'PARROT','name':'givenName'}";

        // when
        Animal actual = objectMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeParrotAsAnimalFromSerializedParrot() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Parrot());

        // when
        Animal actual = objectMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeParrotAsBirdFromJson() throws JsonProcessingException {
        // given
        String json = "{'type':'PARROT'}";

        // when
        Bird actual = objectMapper.readValue(json, Bird.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeParrotAsBirdFromSerializedParrot() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(new Parrot());

        // when
        Bird actual = objectMapper.readValue(json, Bird.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeListOfAnimals() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(List.of(new Human("givenName"), new Parrot()));

        // when
        List<Animal> actual = objectMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(List.of(new Human("givenName"), new Parrot()));
    }

    @Test
    void shouldDeserializeListOfMammals() throws JsonProcessingException {
        // given
        String json = objectMapper.writeValueAsString(List.of(new Human("givenName")));

        // when
        List<Mammal> actual = objectMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(List.of(new Human("givenName")));
    }

    @Test
    void shouldDeserializeHumanAsHumanFromJson() throws JsonProcessingException {
        // given
        String json = "{'animalType':'HUMAN','name':'givenName'}";

        // when
        Human actual = objectMapper.readValue(json, Human.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    sealed interface Animal extends SealedSimpleJsonHierarchies {

    }

    sealed interface Bird extends Animal, SimpleJsonHierarchy<BirdType> {

    }

    @Getter
    @AllArgsConstructor
    enum BirdType implements TypeProvider<Bird> {
        PARROT(Parrot.class);

        private final Class<? extends Bird> type;
    }

    record Parrot() implements Bird {

        @Override
        public BirdType getType() {
            return BirdType.PARROT;
        }
    }

    sealed interface Mammal extends Animal, SimpleJsonHierarchy<MammalType> {

    }

    @Getter
    @AllArgsConstructor
    enum MammalType implements TypeProvider<Mammal> {
        HUMAN(Human.class);

        private final Class<? extends Mammal> type;
    }

    record Human(String name) implements Mammal {

        @Override
        public MammalType getType() {
            return MammalType.HUMAN;
        }
    }
}
