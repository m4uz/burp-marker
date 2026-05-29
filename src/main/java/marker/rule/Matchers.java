package marker.rule;

import java.util.List;
import java.util.function.BiFunction;

public final class Matchers {
    private Matchers() {
    }

    public static final BiFunction<String, String, Boolean> REGEX = String::matches;
    public static final BiFunction<Boolean, Boolean, Boolean> BOOLEAN_EQUALS = Boolean::equals;
    public static final BiFunction<Short, Short, Boolean> SHORT_EQUALS = Short::equals;
    public static final BiFunction<List<String>, String, Boolean> ANY_REGEX =
            (values, pattern) -> values.stream().anyMatch(value -> value.matches(pattern));
}
