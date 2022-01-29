package io.apicurio.bot.actions;

import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import org.jboss.logging.Logger;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RemoveNeedsTriageLabelFromClosedIssue {

    private static final Logger LOG = Logger.getLogger(RemoveNeedsTriageLabelFromClosedIssue.class);

    @Inject
    ApicurioBotProperties properties;

    public void handle(ApicurioBotConfigFile config, GHEventPayload.Issue issuePayload) throws IOException {
        GHIssue issue = issuePayload.getIssue();
        for (GHLabel label : issue.getLabels()) {
            if (label.getName().equals(config.triage.needsTriageLabel)) {
                if (!properties.isDryRun()) {
                    issue.removeLabels(config.triage.needsTriageLabel);
                } else {
                    LOG.info("Issue #" + issue.getNumber() + " - Remove label: " + config.triage.needsTriageLabel);
                }
                break;
            }
        }
    }
}
