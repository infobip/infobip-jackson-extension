package com.infobip.jackson.dynamic;

import com.infobip.jackson.TestBase;
import com.infobip.jackson.TypeProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
class MultiHierarchyDynamicDeserializerTest extends TestBase {

    @Override
    protected JsonMapper.Builder customize(JsonMapper.Builder builder) {
        DynamicHierarchyModule module = new DynamicHierarchyModule();
        module.addDeserializer(Animal.class, DynamicHierarchyDeserializer.from(AnimalType.class, "animalType"));
        module.addDeserializer(Mammal.class, DynamicHierarchyDeserializer.from(MammalType.class, "mammalType"));
        return builder.addModules(module);
    }

    @Test
    void shouldDeserializeHumanAsAnimalFromJson() {
        // given
        String json = "{'animalType':'MAMMAL','mammalType':'HUMAN','name':'givenName'}";

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
        String json = "{'mammalType':'HUMAN','name':'givenName'}";

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
    void shouldDeserializeListOfAnimals() {
        // given
        String json = jsonMapper.writeValueAsString(List.of(new Human("givenName")));

        // when
        List<Animal> actual = jsonMapper.readValue(json, new TypeReference<>() {
        });

        // then
        then(actual).isEqualTo(List.of(new Human("givenName")));
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
        String json = "{'animalType':'MAMMAL','mammalType':'HUMAN','name':'givenName'}";

        // when
        Human actual = jsonMapper.readValue(json, Human.class);

        // then
        then(actual).isEqualTo(new Human("givenName"));
    }

    interface Animal {

        AnimalType getAnimalType();

    }

    interface Mammal extends Animal {

        MammalType getMammalType();

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
