package com.infobip.jackson;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SealedJsonHierarchiesTypeResolver implements JsonTypeResolver {

    private final String typePropertyName;
    private final Map<String, ? extends SimpleJsonTypeResolver<?>> typeValueToResolver;

    public SealedJsonHierarchiesTypeResolver(String typePropertyName,
                                             Map<String, ? extends SimpleJsonTypeResolver<?>> typeValueToResolver) {
        this.typePropertyName = typePropertyName;
        this.typeValueToResolver = typeValueToResolver;
    }

    public static SealedJsonHierarchiesTypeResolver of(Class<?> parentType,
                                                       Function<Class<?>, Optional<JsonTypeResolver>> jsonTypeResolverFactory) {
        var permittedSubclasses = parentType.getPermittedSubclasses();

        if (Objects.isNull(permittedSubclasses)) {
            throw new IllegalArgumentException(parentType + " is not a sealed hierarchy");
        }

        var resolvers = Stream.of(permittedSubclasses)
                              .map(jsonTypeResolverFactory)
                              .flatMap(Optional::stream)
                              .toList();

        for (JsonTypeResolver resolver : resolvers) {
            if (!(resolver instanceof SimpleJsonTypeResolver<?>)) {
                throw new IllegalArgumentException(
                        parentType + " has a sub hierarchy which does not use SimpleJsonTypeResolver, this is currently unsupported");
            }
        }

        var simpleResolvers = resolvers.stream()
                                       .map(resolver -> (SimpleJsonTypeResolver<?>) resolver)
                                       .toList();

        if (simpleResolvers.isEmpty()) {
            throw new IllegalArgumentException(parentType + " does not have any resolvable sub hierarchies");
        }

        var firstSimpleResolver = simpleResolvers.get(0);
        var typePropertyName = firstSimpleResolver.getTypePropertyName();

        for (SimpleJsonTypeResolver<?> simpleResolver : simpleResolvers) {
            if (!simpleResolver.getTypePropertyName().equals(typePropertyName)) {
                throw new IllegalArgumentException(
                        simpleResolver.getType() + " does not use the expected type property name. Expected " + typePropertyName +
                                " Got " + firstSimpleResolver.getTypePropertyName());
            }
        }

        requireNoDuplicates(simpleResolvers);

        var typeValueToResolver = simpleResolvers.stream()
                                                 .flatMap(resolver -> Stream.of(resolver.getType().getEnumConstants())
                                                                            .map(String::valueOf)
                                                                            .collect(Collectors.toMap(
                                                                                    Function.identity(),
                                                                                    b -> resolver))
                                                                            .entrySet()
                                                                            .stream())
                                                 .collect(Collectors.toMap(Map.Entry::getKey,
                                                                           Map.Entry::getValue));

        return new SealedJsonHierarchiesTypeResolver(typePropertyName, typeValueToResolver);
    }

    private static void requireNoDuplicates(List<? extends SimpleJsonTypeResolver<?>> simpleResolvers) {
        var enumValues = new HashSet<String>();
        simpleResolvers.forEach(resolver -> Stream.of(resolver.getType().getEnumConstants())
                                                  .map(String::valueOf)
                                                  .forEach(type -> {
                                                      if (enumValues.contains(type)) {
                                                          throw new IllegalArgumentException(
                                                                  "Duplicate type in multi hierarchy" + type);
                                                      }
                                                      enumValues.add(String.valueOf(type));
                                                  }));
    }

    @Override
    public Class<?> resolve(Map<String, Object> json) {
        var value = String.valueOf(json.get(typePropertyName));
        SimpleJsonTypeResolver<?> simpleJsonTypeResolver = typeValueToResolver.get(value);
        return simpleJsonTypeResolver.resolve(json);
    }
}