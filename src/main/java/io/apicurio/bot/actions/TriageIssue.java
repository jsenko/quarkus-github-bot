package io.apicurio.bot.actions;

import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.apicurio.bot.config.ApicurioBotConfigFile.TriageRule;
import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.util.Collections;
import io.apicurio.bot.util.Expressions;
import io.apicurio.bot.util.GHIssues;
import io.apicurio.bot.util.Labels;
import io.apicurio.bot.util.Patterns;
import io.apicurio.bot.util.Templates;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TriageIssue {

    private static final Logger LOG = LoggerFactory.getLogger(TriageIssue.class);

    //private static final int LABEL_SIZE_LIMIT = 95;

    @Inject
    ApicurioBotProperties properties;

    @Inject
    Expressions expressionEval;

    public void triageOpenedIssue(ApicurioBotConfigFile config, GHEventPayload.Issue payload) throws IOException {

        GHIssue issue = payload.getIssue();
        Set<String> labels = new HashSet<>();
        Set<String> mentions = new HashSet<>();

        List<TriageRule> rules = config.triage.rules;
        for (int i = 0, rulesSize = rules.size(); i < rulesSize; i++) {
            TriageRule rule = rules.get(i);
            if (matchRule(issue, rule)) {
                LOG.debug("Issue #{} matches rule #{}", issue.getNumber(), i);
                labels.addAll(rule.labels);
                for (String mention : rule.notify) {
                    if (!mention.equals(issue.getUser().getLogin())) {
                        mentions.add(mention);
                    }
                }
            }
        }

        /*
         * Needs triage if there are no relevant mentions from the rules,
         * and there are no new or existing area labels.
         * Do we want to notify somebody else even when the originator of the issue is among default reviewers?
         */
        if (mentions.isEmpty() && !hasAreaOrTriageLabels(labels) && !GHIssues.hasAreaLabel(issue)) { // TODO fix last two
            if (!config.triage.defaultNotify.contains(issue.getUser().getLogin())) {
                var reviewer = Collections.randomPick(config.triage.defaultNotify);
                reviewer.ifPresent(mentions::add);
            }
            labels.add(config.triage.needsTriageLabel);
        }

        // Comment
        if (!mentions.isEmpty()) {
            var comment = Templates.triageIssueWelcome(mentions).render();
            if (!properties.isDryRun()) {
                issue.comment(comment);
            } else {
                LOG.info("Issue #{} - Add comment: {}", issue.getNumber(), comment);
            }
        }

        // Add labels
        if (!labels.isEmpty()) {
            if (!properties.isDryRun()) {
                issue.addLabels(labels.toArray(new String[0]));
            } else {
                LOG.info("Issue #{} - Add labels: {}", issue.getNumber(), String.join(", ", labels));
            }
        }
    }

    private boolean hasAreaOrTriageLabels(Set<String> labels) {
        for (String label : labels) {
            if (label.startsWith(Labels.AREA_PREFIX) || label.startsWith(Labels.TRIAGE_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchRule(GHIssue issue, TriageRule rule) {

        // Patterns
        if (rule.patterns != null) {

            Set<String> anywhere = new HashSet<>(rule.patterns.anywhere);
            Set<String> title = new HashSet<>(rule.patterns.title);
            title.addAll(anywhere);
            Set<String> body = new HashSet<>(anywhere); // TODO

            for (String pattern : title) {
                if (Patterns.find(pattern, issue.getTitle())) {
                    return true;
                }
            }

            for (String pattern : body) {
                if (Patterns.find(pattern, issue.getBody())) {
                    return true;
                }
            }
        }

        // Expressions
        if (rule.expressions != null) {
            for (String expression : rule.expressions) {
                try {

                    Map<String, Object> data = new HashMap<>();
                    data.put("title", issue.getTitle());
                    data.put("body", issue.getBody());
                    return expressionEval.evaluateBoolean(expression, data);
                } catch (Exception ex) {
                    LOG.error("Error evaluating expression '" + expression + "'", ex);
                }
            }

            return false;
        }

        return false;
    }
}
