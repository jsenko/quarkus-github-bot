package io.apicurio.bot.util;

import java.util.regex.Pattern;

public final class Patterns {

    public static boolean find(String pattern, String string) {
        if (!Validation.notBlank(pattern) || !Validation.notBlank(string))
            return false; // TODO warn
        return Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(string)
                .find();
    }

    private Patterns() {
    }
}
