# Infobip Jackson Extension

![](https://github.com/infobip/infobip-jackson-extension/workflows/maven/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-jackson-extension-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-jackson-extension-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/infobip/infobip-jackson-extension/badge.svg?branch=master)](https://coveralls.io/github/infobip/infobip-jackson-extension?branch=master)
[![Known Vulnerabilities](https://snyk.io/test/github/infobip/infobip-jackson-extension/badge.svg)](https://snyk.io/test/github/infobip/infobip-jackson-extension)

Library which provides new features for (de)serialization on top of [Jackson library](https://github.com/FasterXML/jackson).

## Contents

1. [Changelog](#Changelog)
1. [Setup](#Setup)
1. [Features](#Features)
    * [Simple json hierarchy](#SimpleJsonHierarchy)
    * [Overriding type json property name](#OverridingTypeJsonPropertyName)
    * [Lower case type value](#LowerCaseTypeValue)
    * [Multi level hierarchies](#MultiLevelHierarchies)
      * [Hierarchy per property approach](#HierarchyPerPropertyApproach)
      * [Single property approach](#SinglePropertyApproach)
    * [Parallel hierarchies](#ParallelHierarchies)
    * [Typeless (present property)](#Typeless)
    * [Dynamic hierarchy](#DynamicHierarchy)
    * [Single Argument Property Creator annotationless support](#SingleArgumentPropertyCreatorAnnotationlessSupport)

<a id="Changelog"></a>
## Changelog

For changes check the [changelog](CHANGELOG.md).

<a id="Setup"></a>
## Setup

All examples have corresponding tests and additional usage examples can be found in tests.

In examples, tests and [Single Argument Property Creator annotationless support](#SingleArgumentPropertyCreatorAnnotationlessSupport) it's required that 
the code is compiled with `-parameters` compiler option and that `jackson-module-parameter-names` module is used.

Parameter names module:
```xml
<dependency>
   <groupId>com.fasterxml.jackson.module</groupId>
   <artifactId>jackson-module-parameter-names</artifactId>
</dependency>
```

Compiler plugin setup:
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<version>${maven-compiler-plugin.version}</version>
	<configuration>
       ...
		<compilerArgument>-parameters</compilerArgument>
		<testCompilerArgument>-parameters</testCompilerArgument>
       ...
	</configuration>
</plugin>
```

Parameter names module makes parameter names visible to the Jackson meaning it can map json properties to constructor parameter names so there's no redundant 
Jackson annotation to redeclare them. There's an important catch here: JSON field name, Java accessors and constructor parameter name all must match!

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

<a id="Features"></a>
## Features

<a id="SimpleJsonHierarchy"></a>
### Simple json hierarchy

For models that have a type represented by an enum you can use simple typed json approach:

```java
interface FooBar extends SimpleJsonHierarchy<FooBarType> {
}

record Foo(String foo) implements FooBar {

   @Override
   public FooBarType getType() {
      return FooBarType.FOO;
   }

}

record Bar(String bar) implements FooBar {

   @Override
   public FooBarType getType() {
      return FooBarType.BAR;
   }

}

@Getter
@AllArgsConstructor
enum FooBarType implements TypeProvider<FooBar> {
   FOO(Foo.class),
   BAR(Bar.class);

   private final Class<? extends FooBar> type;
}
```

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/SimpleJsonHierarchyDeserializerTest.java)

<a id="OverridingTypeJsonPropertyName"></a>
#### Overriding type json property name

Name of the property can be overridden:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/CustomTypeFieldSimpleJsonHierarchyTest.java)

<a id="LowerCaseTypeValue"></a>
#### Lower case type value

Casing of the property type value can be overridden:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/LowerCaseTypeSimpleJsonHierarchyTest.java)

<a id="MultiLevelHierarchies"></a>
### Multi level hierarchies

Multi level hierarchies are supported with 2 different approach.

The first approach is used when there is a property per hierarchy.

The second approach is used when only one property is shared, but property values are globally unique, meaning 
there can't be two hierarchies that have the same property value. 
If values were not unique, it would be unclear to which class to deserialize.

Note that these examples have simplified sub hierarchies for brevity of the documentation, but you can use different 
mechanisms previously defined for specific sub hierarchy - e.g. one hierarchy can use the default type property
but another one can [override type json property name](#OverridingTypeJsonPropertyName).

<a id="HierarchyPerPropertyApproach"></a>
When there is a property per hierarchy following can be used:

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
```

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/MultiHierarchyJsonTypedDeserializerTest.java)

<a id="SinglePropertyApproach"></a>
When there is a single property shared for all hierarchies following can be used:

```java
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
```

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/SealedSimpleJsonHierarchiesTest.java)

<a id="ParallelHierarchies"></a>
### Parallel hierarchies

In case you have multiple hierarchies that reuse the same enum TypeProvider can present an issue, use this approach to as a guide to possible solution:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/ParallelHierarchyJsonTypedDeserializerTest.java).

<a id="Typeless"></a>
### Typeless (present property)

In case you don't want to (or can't - third party API) include type information in json, you can use this approach:

```java
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
``` 
Notice standard jackson `@JsonNaming` annotation in `Bike` interface.  

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/PresentPropertyCaseFormatDeserializerTest.java).

<a id="DynamicHierarchy"></a>
### Dynamic hierarchy

In case root of the hierarchy is decoupled from the leaves in different compilation units.
Also it can be seen as annotationless alternative.

[Showcase without type provider](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/dynamic/DynamicHierarchyDeserializerTest.java).

[Showcase with type provider](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/dynamic/DynamicHierarchyDeserializerWithTypeProviderTest.java).

In case Spring Boot starter is used you only need to register DynamicHierarchyDeserializer as a @Bean, starter handles wiring with Jackson.

<a id="SingleArgumentPropertyCreatorAnnotationlessSupport"></a>
### Single Argument Property Creator annotationless support

This module also adds support for deserializing single property value objects when using parameter names module:

```java
class Foo {
    private final Bar bar;

    Foo(Bar bar) {
        this.bar = bar;
    }
}
```

without any additional configuration or annotations.
Related issues: https://github.com/FasterXML/jackson-databind/issues/1498, https://github.com/spring-projects/spring-boot/issues/26023.

## <a id="Requirements"></a> Requirements:

- Java 17

## <a id="Contributing"></a> Contributing

If you have an idea for a new feature or want to report a bug please use the issue tracker.

Pull requests are welcome!

## <a id="License"></a> License

This library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
