package io.apicurio.bot.routes;

import io.apicurio.bot.actions.TriageIssue;
import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.Issue.Closed;
import io.apicurio.bot.actions.RemoveNeedsTriageLabelFromClosedIssue;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import org.kohsuke.github.GHEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static io.quarkiverse.githubapp.event.Issue.Opened;

public class Issue {

    private static final Logger LOG = LoggerFactory.getLogger(Issue.class);

    @Inject
    TriageIssue triage;

    @Inject
    RemoveNeedsTriageLabelFromClosedIssue triageCleanup;

    void onIssueCreated(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
            @Opened GHEventPayload.Issue payload) {
        if (config == null) {
            LOG.debug("Unable to find '.github/{}' file for '{}'",
                    ApicurioBotConfigFile.NAME, payload.getRepository().getFullName());
            return;
        }
        if (!config.valid()) {
            LOG.debug("Configuration file for '{}' is not valid",
                    payload.getRepository().getFullName());
            return;
        }
        try {
            triage.triageOpenedIssue(config, payload);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }

    void onIssueClosed(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
            @Closed GHEventPayload.Issue payload) {
        if (config == null) {
            LOG.debug("Unable to find '.github/{}' file for '{}'",
                    ApicurioBotConfigFile.NAME, payload.getRepository().getFullName());
            return;
        }
        if (!config.valid()) {
            LOG.debug("Configuration file for '{}' is not valid",
                    payload.getRepository().getFullName());
            return;
        }
        try {
            triageCleanup.handle(config, payload);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }
}
