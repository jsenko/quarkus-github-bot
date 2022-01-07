package io.apicurio.bot.util;

import io.apicurio.bot.config.ApicurioBotConfigFile;

import java.util.Set;
import java.util.stream.Collectors;

public final class Labels {

    public static Set<String> getAreaLabels(ApicurioBotConfigFile config) {
        return config.triage.rules.stream().flatMap(r -> r.labels.stream()).collect(Collectors.toSet());
    }

    public static boolean containsAreaLabels(ApicurioBotConfigFile config, Set<String> labels) {
        // TODO Cache this
        var targetLabels = getAreaLabels(config);
        for (String label : labels) {
            if (targetLabels.contains(label)) {
                return true;
            }
        }
        return false;
    }

    private Labels() {
    }
}
