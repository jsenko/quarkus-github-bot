package io.quarkus.bot.util;

import java.util.Arrays;
import java.util.function.Predicate;

public final class Validation {

    public static boolean all(Boolean... valid) {
        return Arrays.stream(valid).allMatch(x -> x);
    }

    public static boolean notNull(Object value) {
        return value != null;
    }

    public static boolean notBlank(Object value) {
        var string = (String) value;
        return string != null && !string.trim().isEmpty();
    }

    public static <T> boolean nullOr(T value, Predicate<T> validate) {
        if (value != null) {
            return validate.test(value);
        }
        return true;
    }

    public static <T> boolean notNullAnd(T value, Predicate<T> validate) {
        return value != null && validate.test(value);
    }

    private Validation() {
    }
}
