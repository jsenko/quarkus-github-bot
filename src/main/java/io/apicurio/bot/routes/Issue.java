package io.apicurio.bot.routes;

import io.apicurio.bot.actions.RemoveNeedsTriageLabel;
import io.apicurio.bot.actions.TriageIssue;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.Issue.Closed;
import io.quarkiverse.githubapp.event.Issue.Edited;
import io.quarkiverse.githubapp.event.Issue.Labeled;
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
    RemoveNeedsTriageLabel triageCleanup;

    @Inject
    Validate validate;

    void onIssueCreated(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
                        @Opened GHEventPayload.Issue payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            triage.triageOpenedIssue(config, payload);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }

    void onIssueEdited(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
                       @Edited @Labeled GHEventPayload.Issue payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            triageCleanup.handle(config, payload.getIssue(), false);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }

    void onIssueClosed(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
                       @Closed GHEventPayload.Issue payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            triageCleanup.handle(config, payload.getIssue(), true);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }
}
