package io.apicurio.bot.actions;

import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.util.Labels;
import org.jboss.logging.Logger;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;

import java.io.IOException;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RemoveNeedsTriageLabel {

    private static final Logger LOG = Logger.getLogger(RemoveNeedsTriageLabel.class);

    @Inject
    ApicurioBotProperties properties;

    /**
     * Remove the triage label if the issue or the PR is closed,
     * or there are area labels.
     */
    public void handle(ApicurioBotConfigFile config, GHIssue issue, boolean isClosed) throws IOException {
        var labels = issue.getLabels().stream().map(GHLabel::getName).collect(Collectors.toSet());
        if ((isClosed || Labels.containsAreaLabels(config, labels)) &&
                labels.contains(config.triage.needsTriageLabel)) {
            if (!properties.isDryRun()) {
                issue.removeLabels(config.triage.needsTriageLabel);
            } else {
                LOG.info("Issue or PR #" + issue.getNumber() + " - Remove label: " + config.triage.needsTriageLabel);
            }
        }
    }
}

