package io.quarkus.bot.util;

import java.util.regex.Pattern;

import static io.quarkus.bot.util.Validation.notBlank;

public final class Patterns {

    public static boolean find(String pattern, String string) {
        if (!notBlank(pattern) || !notBlank(string))
            return false; // TODO warn
        return Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(string)
                .find();
    }

    private Patterns() {
    }
}
