package io.apicurio.bot.actions;

import io.apicurio.bot.cache.NotificationCache;
import io.apicurio.bot.cache.PullRequestNotification;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.util.Collections;
import io.apicurio.bot.util.Labels;
import io.apicurio.bot.util.Templates;
import io.apicurio.bot.util.TriageRules;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHPullRequest;
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
public class TriagePullRequest {

    private static final Logger LOG = LoggerFactory.getLogger(TriagePullRequest.class);

    @Inject
    TriageRules triageMatcher;

    @Inject
    ApicurioBotProperties properties;

    @Inject
    NotificationCache ncache;

    public void triageOpenedPR(ApicurioBotConfigFile config, GHEventPayload.PullRequest payload) throws IOException {

        // TODO Fix duplicated code, since PullRequest is a subclass of Issue
        // The main difference is that for PRs, directories are also matched, and there is a different comment message

        GHPullRequest pr = payload.getPullRequest();
        Set<String> labels = new HashSet<>();
        Set<String> mentions = new HashSet<>();
        boolean isTriage = false;

        List<ApicurioBotConfigFile.TriageRule> rules = config.triage.rules;
        for (int i = 0, rulesSize = rules.size(); i < rulesSize; i++) {
            ApicurioBotConfigFile.TriageRule rule = rules.get(i);
            if (matchRule(pr, rule)) {
                LOG.debug("PR #{} matches rule #{}", pr.getNumber(), i);
                labels.addAll(rule.labels);
                for (String mention : rule.notify) {
                    if (!mention.equals(pr.getUser().getLogin())) {
                        mentions.add(mention);
                    }
                }
            }
        }

        /*
         * Needs triage if there are no new or existing area labels.
         */
        if (!Labels.containsAreaLabels(config, labels) &&
                !Labels.containsAreaLabels(config, pr.getLabels().stream().map(GHLabel::getName).collect(Collectors.toSet()))) {
            // But if there's somebody in the mentions already, they should do the triage
            if (mentions.isEmpty() && !config.triage.defaultNotify.contains(pr.getUser().getLogin())) {
                var reviewer = Collections.randomPick(config.triage.defaultNotify);
                reviewer.ifPresent(mentions::add);
            }
            labels.add(config.triage.needsTriageLabel);
            isTriage = true;
        }

        // Comment
        if (!mentions.isEmpty()) {
            var comment = Templates.triagePRWelcome(mentions).render();
            ncache.getPullRequests().add(PullRequestNotification.builder()
                    .title(pr.getTitle())
                    .isTriage(isTriage)
                    .url(pr.getUrl().toString())
                    .mentions(new ArrayList<>(mentions))
                    .build());
            if (!properties.isDryRun()) {
                pr.comment(comment);
            } else {
                LOG.info("PR #{} - Add comment: {}", pr.getNumber(), comment);
            }
        }

        // Add labels
        if (!labels.isEmpty()) {
            if (!properties.isDryRun()) {
                pr.addLabels(labels.toArray(new String[0]));
            } else {
                LOG.info("PR #{} - Add labels: {}", pr.getNumber(), String.join(", ", labels));
            }
        }
    }

    private boolean matchRule(GHPullRequest pr, ApicurioBotConfigFile.TriageRule rule) {
        return triageMatcher.matchRuleByPatterns(pr, rule) ||
                triageMatcher.matchRuleByExpressions(pr, rule) ||
                triageMatcher.matchRuleByDirectories(pr, rule);
    }
}
