package io.apicurio.bot.routes;

import io.apicurio.bot.actions.SetLabelColor;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.Label.Created;
import io.quarkiverse.githubapp.event.Label.Edited;
import org.kohsuke.github.GHEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class Label {

    private static final Logger LOG = LoggerFactory.getLogger(Label.class);

    @Inject
    SetLabelColor setAreaLabelColor;

    @Inject
    Validate validate;

    void onLabelCreatedEdited(@ConfigFile(ApicurioBotConfigFile.NAME) ApicurioBotConfigFile config,
            @Created @Edited GHEventPayload.Label payload) {
        if (!validate.validate(config, payload)) {
            return;
        }
        try {
            setAreaLabelColor.handle(config, payload);
        } catch (Exception ex) {
            LOG.error("TODO", ex);
        }
    }
}
