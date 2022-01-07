package io.quarkus.bot.util;

import static io.quarkus.bot.util.Validation.notBlank;

public final class Matcher {

    public static boolean matches(String pattern, String string) {
        if (notBlank(string)) {
            return Patterns.find(pattern, string);
        }

        return false;
    }

    private Matcher() {
    }
}
