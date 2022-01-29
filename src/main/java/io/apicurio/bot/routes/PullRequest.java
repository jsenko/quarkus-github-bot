package io.apicurio.bot.routes;

import io.apicurio.bot.actions.RemoveNeedsTriageLabel;
import io.apicurio.bot.actions.TriagePullRequest;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.PullRequest.Closed;
import io.quarkiverse.githubapp.event.PullRequest.Edited;
import io.quarkiverse.githubapp.event.PullRequest.Labeled;
import org.kohsuke.github.GHEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static io.quarkiverse.githubapp.event.Issue.Opened;

public class PullRequest {

    private static final Logger LOG = LoggerFactory.getLogger(PullRequest.class);

    @Inject
    TriagePullRequest triage;

    @Inject
    RemoveNeedsTriageLabel triageCleanup;

    @Inject
    Validate validate;

    void onPRCreated(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
                     @Opened GHEventPayload.PullRequest payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            triage.triageOpenedPR(config, payload);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }

    void onPREdited(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
                    @Edited @Labeled GHEventPayload.PullRequest payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            triageCleanup.handle(config, payload.getPullRequest(), false);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }

    void onPRClosed(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
                    @Closed GHEventPayload.PullRequest payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            triageCleanup.handle(config, payload.getPullRequest(), true);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }
}
