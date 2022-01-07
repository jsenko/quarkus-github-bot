package io.quarkus.bot.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.bot.util.Validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.quarkus.bot.util.Validation.*;

public class ApicurioBotConfigFile {

    public static final String NAME = "apicurio-bot.yaml";

    public LabelConfig labels;

    public TriageConfig triage;

    public static class LabelConfig {

        public String areaLabelColor;

        public String triageLabelColor;

        public boolean valid() {
            return all(
                    notBlank(areaLabelColor),
                    notBlank(triageLabelColor));
        }
    }

    public static class TriageConfig {

        public String needsTriageLabel;

        @JsonDeserialize(as = HashSet.class)
        public Set<String> defaultNotify = new HashSet<>();

        public List<TriageRule> rules = new ArrayList<>();

        public boolean valid() {
            return all(
                    notBlank(needsTriageLabel),
                    !defaultNotify.isEmpty(),
                    defaultNotify.stream().allMatch(Validation::notBlank),
                    rules.stream().allMatch(TriageRule::valid));
        }
    }

    public static class TriageRule {

        @JsonDeserialize(as = HashSet.class)
        public Set<String> expressions = new HashSet<>();

        public TriagePatterns patterns;

        @JsonDeserialize(as = HashSet.class)
        public Set<String> labels = new HashSet<>();

        @JsonDeserialize(as = HashSet.class)
        public Set<String> notify = new HashSet<>();

        public boolean valid() {
            return all(
                    expressions.stream().allMatch(Validation::notBlank),
                    labels.stream().allMatch(Validation::notBlank),
                    notify.stream().allMatch(Validation::notBlank),
                    nullOr(patterns, TriagePatterns::valid));
        }
    }

    public static class TriagePatterns {

        @JsonDeserialize(as = HashSet.class)
        public Set<String> anywhere = new HashSet<>();

        @JsonDeserialize(as = HashSet.class)
        public Set<String> title = new HashSet<>();

        public boolean valid() {
            return all(
                    anywhere.stream().allMatch(Validation::notBlank),
                    title.stream().allMatch(Validation::notBlank));
        }
    }

    public boolean valid() {
        return all(
                notNullAnd(labels, LabelConfig::valid),
                notNullAnd(triage, TriageConfig::valid));
    }
}
