# Infobip Jackson Extension

[![Build Status](https://travis-ci.org/infobip/infobip-jackson-extension.svg?branch=master)](https://travis-ci.org/infobip/infobip-jackson-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-jackson-extension-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-jackson-extension-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/infobip/infobip-jackson-extension/badge.svg?branch=master)](https://coveralls.io/github/infobip/infobip-jackson-extension?branch=master)

Library which provides new features for (de)serialization on top of [Jackson library](https://github.com/FasterXML/jackson).

## Contents

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

In case you need the functionality without Spring Boot you can do the wiring manually:

```java
objectMapper.registerModule(new InfobipJacksonModule());     
```

<a name="Features"></a>
## Features

<a name="SimpleJsonHierarchy"></a>
### Simple json hierarchy

For models that have a type represented by an enum you can use simple typed json approach:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/SimpleJsonHierarchyDeserializerTest.java)

<a name="OverridingTypeJsonPropertyName"></a>
#### Overriding type json property name

Name of the property can be overridden:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/CustomTypeFieldSimpleJsonHierarchyTest.java)

<a name="LowerCaseTypeValue"></a>
#### Lower case type value

Casing of the property type value can be overridden:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/LowerCaseTypeSimpleJsonHierarchyRmiTest.java)

<a name="MultiLevelHierarchies"></a>
### Multi level hierarchies

If you have multiple levels of hierarchy following approach can be used:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/MultiHierarchyJsonTypedDeserializerTest.java)

<a name="ParallelHierarchies"></a>
### Parallel hierarchies

In case you have multiple hierarchies that reuse the same enum TypeProvider can present an issue, use this approach to as a guide to possible solution:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/ParallelHierarchyJsonTypedDeserializerTest.java).

<a name="Typeless"></a>
### Typeless (present property)

In case you don't want to (or can't - third party API) include type information in json, you can use this approach:

[Showcase](infobip-jackson-extension-module/src/test/java/com/infobip/jackson/PresentPropertyDeserializerTest.java).

## <a name="Requirements"></a> Requirements:

- Java 8

## <a name="Contributing"></a> Contributing

If you have an idea for a new feature or want to report a bug please use the issue tracker.

Pull requests are welcome!

## <a name="License"></a> License

This library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
