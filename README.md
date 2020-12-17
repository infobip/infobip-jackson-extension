# Infobip Jackson Extension

![](https://github.com/infobip/infobip-jackson-extension/workflows/maven/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-jackson-extension-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-jackson-extension-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/infobip/infobip-jackson-extension/badge.svg?branch=master)](https://coveralls.io/github/infobip/infobip-jackson-extension?branch=master)

Library which provides new features for (de)serialization on top of [Jackson library](https://github.com/FasterXML/jackson).

## Contents

* [Setup](#Setup)
* [Features](#Features)
    * [Simple json hierarchy](#SimpleJsonHierarchy)
    * [Overriding type json property name](#OverridingTypeJsonPropertyName)
    * [Lower case type value](#LowerCaseTypeValue)
    * [Multi level hierarchies](#MultiLevelHierarchies)
    * [Parallel hierarchies](#ParallelHierarchies)
    * [Typeless (present property)](#Typeless)
    
<a name="Setup"></a>
## Setup

All examples have corresponding tests and additional usage examples can be found in tests.

### Spring Boot

Just include the following dependency:

```xml
<dependency>
    <groupId>com.infobip</groupId>
    <artifactId>infobip-jackson-extension-spring-boot-starter</artifactId>
    <version>${infobip-jackson-extension.version}</version>
</dependency>
```

### Without Spring Boot

Include the following dependency:

```xml
<dependency>
    <groupId>com.infobip</groupId>
    <artifactId>infobip-jackson-extension-module</artifactId>
    <version>${infobip-jackson-extension.version}</version>
</dependency>
```

Register the module with `ObjectMapper`:

```java
objectMapper.registerModule(new InfobipJacksonModule());     
```

<a name="Features"></a>
## Features

<a name="SimpleJsonHierarchy"></a>
### Simple json hierarchy

For models that have a type represented by an enum you can use simple typed json approach:

```java
interface FooBar extends SimpleJsonHierarchy<FooBarType> {
}

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class Foo implements FooBar {
    private final String foo;
    private final FooBarType type = FooBarType.FOO;
}

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class Bar implements FooBar {
    private final String bar;
    private final FooBarType type = FooBarType.BAR;
}

@Getter
@AllArgsConstructor
enum FooBarType implements TypeProvider {
    FOO(Foo.class),
    BAR(Bar.class);

    private final Class<? extends FooBar> type;
}
```

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/SimpleJsonHierarchyDeserializerTest.java)

<a name="OverridingTypeJsonPropertyName"></a>
#### Overriding type json property name

Name of the property can be overridden:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/CustomTypeFieldSimpleJsonHierarchyTest.java)

<a name="LowerCaseTypeValue"></a>
#### Lower case type value

Casing of the property type value can be overridden:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/LowerCaseTypeSimpleJsonHierarchyTest.java)

<a name="MultiLevelHierarchies"></a>
### Multi level hierarchies

If you have multiple levels of hierarchy following approach can be used:

```java
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

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class Human implements Mammal {
    private final String name;
    private final AnimalType animalType = AnimalType.MAMMAL;
    private final MammalType mammalType = MammalType.HUMAN;
}

@Getter
@AllArgsConstructor
enum AnimalType implements TypeProvider {
    MAMMAL(Mammal.class);

    private final Class<? extends Animal> type;
}

@Getter
@AllArgsConstructor
enum MammalType implements TypeProvider {
    HUMAN(Human.class);

    private final Class<? extends Mammal> type;
}
```

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/MultiHierarchyJsonTypedDeserializerTest.java)

<a name="ParallelHierarchies"></a>
### Parallel hierarchies

In case you have multiple hierarchies that reuse the same enum TypeProvider can present an issue, use this approach to as a guide to possible solution:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/ParallelHierarchyJsonTypedDeserializerTest.java).

<a name="Typeless"></a>
### Typeless (present property)

In case you don't want to (or can't - third party API) include type information in json, you can use this approach:

```java
interface Bike extends PresentPropertyJsonHierarchy<BikeType> {
}

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class RoadBike implements Bike {
    private final String roadBike;
}

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class Bmx implements Bike {
    private final String bmx;
}

@Getter
@AllArgsConstructor
enum BikeType implements TypeProvider {
    ROAD_BIKE(RoadBike.class),
    BMX(Bmx.class);

    private final Class<? extends Bike> type;
}
```

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/PresentPropertyDeserializerTest.java).

Notice that by default snake cased type names are translated to camel case properties (e.g. `ROAD_BIKE` -> `roadBike`).
If you want to use different casing you can provide your own resolver by extending `PresentPropertyJsonTypeResolver`:
```java
static class LowerUnderscorePresentPropertyJsonTypeResolver<E extends Enum<E> & TypeProvider> extends PresentPropertyJsonTypeResolver<E> {

    public LowerUnderscorePresentPropertyJsonTypeResolver(Class<E> type) {
        super(type, CaseFormat.LOWER_UNDERSCORE);
    }
}
```
Then your model may look as follows:
```java
@JsonTypeResolveWith(LowerUnderscorePresentPropertyJsonTypeResolver.class)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
interface Bike extends PresentPropertyJsonHierarchy<BikeType> {

}

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class RoadBike implements Bike {

    private final String roadBike;
}

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Value
static class MountainBike implements Bike {

    private final String mountain_bike;
}

@Getter
@AllArgsConstructor
enum BikeType implements TypeProvider {
    ROAD_BIKE(RoadBike.class),
    MOUNTAIN_BIKE(MountainBike.class);

    private final Class<? extends Bike> type;
}
``` 
Notice standard jackson `@JsonNaming` annotation in `Bike` interface.  

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/PresentPropertyCaseFormatDeserializerTest.java).

## <a name="Requirements"></a> Requirements:

- Java 8

## <a name="Contributing"></a> Contributing

If you have an idea for a new feature or want to report a bug please use the issue tracker.

Pull requests are welcome!

## <a name="License"></a> License

This library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
