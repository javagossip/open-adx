package top.openadexchange.commons;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StreamUtils {

    private StreamUtils() {
    }

    public static <S, T> List<T> toList(Collection<S> source, Function<S, T> function) {
        return source.stream().filter(Objects::nonNull).map(function).filter(Objects::nonNull).distinct().toList();
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> source,
            Function<T, K> keyFunction,
            Function<T, V> valueFunction) {
        return source.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyFunction, valueFunction, (a, b) -> a));
    }

    public static <S, T> Set<T> toSet(Collection<S> source, Function<S, T> function) {
        return source.stream()
                .filter(Objects::nonNull)
                .map(function)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
