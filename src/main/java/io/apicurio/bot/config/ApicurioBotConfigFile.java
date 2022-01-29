package io.apicurio.bot.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.apicurio.bot.util.Validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApicurioBotConfigFile {

    public static final String NAME = "apicurio-bot.yaml";

    public LabelConfig labels;

    public TriageConfig triage;

    public static class LabelConfig {

        public String areaLabelColor;

        public String triageLabelColor;

        public boolean valid() {
            return Validation.all(
                    Validation.notBlank(areaLabelColor),
                    Validation.notBlank(triageLabelColor));
        }
    }

    public static class TriageConfig {

        public String needsTriageLabel;

        @JsonDeserialize(as = HashSet.class)
        public Set<String> defaultNotify = new HashSet<>();

        public List<TriageRule> rules = new ArrayList<>();

        public boolean valid() {
            return Validation.all(
                    Validation.notBlank(needsTriageLabel),
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
            return Validation.all(
                    expressions.stream().allMatch(Validation::notBlank),
                    labels.stream().allMatch(Validation::notBlank),
                    notify.stream().allMatch(Validation::notBlank),
                    Validation.nullOr(patterns, TriagePatterns::valid));
        }
    }

    public static class TriagePatterns {

        @JsonDeserialize(as = HashSet.class)
        public Set<String> anywhere = new HashSet<>();

        @JsonDeserialize(as = HashSet.class)
        public Set<String> title = new HashSet<>();

        @JsonDeserialize(as = HashSet.class)
        public Set<String> body = new HashSet<>();

        @JsonDeserialize(as = HashSet.class)
        public Set<String> directories = new HashSet<>();

        public boolean valid() {
            return Validation.all(
                    anywhere.stream().allMatch(Validation::notBlank),
                    title.stream().allMatch(Validation::notBlank),
                    body.stream().allMatch(Validation::notBlank),
                    directories.stream().allMatch(Validation::notBlank));
        }
    }

    public boolean valid() {
        return Validation.all(
                Validation.notNullAnd(labels, LabelConfig::valid),
                Validation.notNullAnd(triage, TriageConfig::valid));
    }
}
