package org.efrenjm.investingtracker.domain.utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionTransformer {
    private CollectionTransformer() {}

    public static <K, V, R> Map<K, R> transformMapValues(
            Map<K, V> source, Function<V, R> transformer) {
        return Optional.ofNullable(source).orElse(Map.of()).entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey, entry -> transformer.apply(entry.getValue())));
    }

    public static <I, V, O> Map<O, V> transformMapKeys(
            Map<I, V> source, Function<I, O> transformer) {
        return Optional.ofNullable(source).orElse(Map.of()).entrySet().stream()
                .collect(
                        Collectors.toMap(
                                entry -> transformer.apply(entry.getKey()), Map.Entry::getValue));
    }

    public static <V, R> Set<R> transform(Set<V> source, Function<V, R> transformer) {
        return Optional.ofNullable(source).orElse(Set.of()).stream()
                .map(transformer)
                .collect(Collectors.toSet());
    }

    public static <V, R> List<R> transform(List<V> source, Function<V, R> transformer) {
        return Optional.ofNullable(source).orElse(List.of()).stream().map(transformer).toList();
    }
}
