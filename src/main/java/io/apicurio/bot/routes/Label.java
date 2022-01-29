package io.apicurio.bot.routes;

import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.Label.Created;
import io.apicurio.bot.actions.SetLabelColor;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import org.kohsuke.github.GHEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class Label {

    private static final Logger LOG = LoggerFactory.getLogger(Label.class);

    @Inject
    SetLabelColor setAreaLabelColor;

    void onLabelCreated(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
            @Created GHEventPayload.Label payload) {
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
            setAreaLabelColor.handle(config, payload);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }
}
