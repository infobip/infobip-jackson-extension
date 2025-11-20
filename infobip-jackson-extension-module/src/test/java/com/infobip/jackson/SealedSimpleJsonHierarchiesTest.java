package com.infobip.jackson;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class SealedSimpleJsonHierarchiesTest extends TestBase {

    @Test
    void shouldDeserializeHumanAsAnimalFromJson() {
        // given
        String json = "{'type':'HUMAN','name':'givenName'}";

        // when
        Animal actual = jsonMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeHumanAsAnimalFromSerializedHuman() {
        // given
        String json = jsonMapper.writeValueAsString(new Human("givenName"));

        // when
        Animal actual = jsonMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeHumanAsMammalFromJson() {
        // given
        String json = "{'type':'HUMAN','name':'givenName'}";

        // when
        Mammal actual = jsonMapper.readValue(json, Mammal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeHumanAsMammalFromSerializedHuman() {
        // given
        String json = jsonMapper.writeValueAsString(new Human("givenName"));

        // when
        Mammal actual = jsonMapper.readValue(json, Mammal.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    @Test
    void shouldDeserializeParrotAsAnimalFromJson() {
        // given
        String json = "{'type':'PARROT','name':'givenName'}";

        // when
        Animal actual = jsonMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeParrotAsAnimalFromSerializedParrot() {
        // given
        String json = jsonMapper.writeValueAsString(new Parrot());

        // when
        Animal actual = jsonMapper.readValue(json, Animal.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeParrotAsBirdFromJson() {
        // given
        String json = "{'type':'PARROT'}";

        // when
        Bird actual = jsonMapper.readValue(json, Bird.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeParrotAsBirdFromSerializedParrot() {
        // given
        String json = jsonMapper.writeValueAsString(new Parrot());

        // when
        Bird actual = jsonMapper.readValue(json, Bird.class);

        // then
        then(actual).isEqualTo(new Parrot());
    }

    @Test
    void shouldDeserializeListOfAnimals() {
        // given
        String json = jsonMapper.writeValueAsString(List.of(new Human("givenName"), new Parrot()));

        // when
        List<Animal> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(List.of(new Human("givenName"), new Parrot()));
    }

    @Test
    void shouldDeserializeListOfMammals() {
        // given
        String json = jsonMapper.writeValueAsString(List.of(new Human("givenName")));

        // when
        List<Mammal> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(List.of(new Human("givenName")));
    }

    @Test
    void shouldDeserializeHumanAsHumanFromJson() {
        // given
        String json = "{'animalType':'HUMAN','name':'givenName'}";

        // when
        Human actual = jsonMapper.readValue(json, Human.class);

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
