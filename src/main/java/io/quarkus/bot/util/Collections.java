package io.quarkus.bot.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public final class Collections {

    private static final Random RANDOM = new Random();

    public static <T> Optional<T> randomPick(Collection<T> items) {
        if (items.isEmpty())
            return Optional.empty();
        int index = RANDOM.nextInt(items.size());
        return items.stream().skip(index).findFirst();
    }

    private Collections() {
    }
}
