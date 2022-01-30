package io.apicurio.bot.actions;

import io.apicurio.bot.cache.IssueNotification;
import io.apicurio.bot.cache.NotificationCache;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.apicurio.bot.config.ApicurioBotConfigFile.TriageRule;
import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.util.Collections;
import io.apicurio.bot.util.Labels;
import io.apicurio.bot.util.Templates;
import io.apicurio.bot.util.TriageRules;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TriageIssue {

    private static final Logger LOG = LoggerFactory.getLogger(TriageIssue.class);

    @Inject
    ApicurioBotProperties properties;

    @Inject
    TriageRules triageMatcher;

    @Inject
    NotificationCache ncache;

    public void triageOpenedIssue(ApicurioBotConfigFile config, GHEventPayload.Issue payload) throws IOException {

        GHIssue issue = payload.getIssue();
        Set<String> labels = new HashSet<>();
        Set<String> mentions = new HashSet<>();
        boolean isTriage = false;

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
         * Needs triage if there are no new or existing area labels.
         */
        if (!Labels.containsAreaLabels(config, labels) &&
                !Labels.containsAreaLabels(config,
                        issue.getLabels().stream().map(GHLabel::getName).collect(Collectors.toSet()))) {
            // But if there's somebody in the mentions already, they should do the triage
            if (mentions.isEmpty() && !config.triage.defaultNotify.contains(issue.getUser().getLogin())) {
                var reviewer = Collections.randomPick(config.triage.defaultNotify);
                reviewer.ifPresent(mentions::add);
            }
            labels.add(config.triage.needsTriageLabel);
            isTriage = true;
        }

        // Comment
        if (!mentions.isEmpty()) {
            var comment = Templates.triageIssueWelcome(mentions).render();
            ncache.getIssues().add(IssueNotification.builder()
                    .title(issue.getTitle())
                    .isTriage(isTriage)
                    .url(issue.getUrl().toString())
                    .mentions(new ArrayList<>(mentions))
                    .build());
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

    private boolean matchRule(GHIssue issue, TriageRule rule) {
        return triageMatcher.matchRuleByPatterns(issue, rule) ||
                triageMatcher.matchRuleByExpressions(issue, rule);

    }
}
