package io.apicurio.bot.routes;

import io.apicurio.bot.config.ApicurioBotConfigFile;
import io.apicurio.bot.config.ApicurioBotProperties;
import org.kohsuke.github.GHEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Validate {

    private static final Logger LOG = LoggerFactory.getLogger(Validate.class);

    @Inject
    ApicurioBotProperties properties;

    public boolean validate(ApicurioBotConfigFile config, GHEventPayload payload) {
        if (config == null) {
            LOG.debug("Unable to find '.github/{}' file for '{}'",
                    ApicurioBotConfigFile.NAME, payload.getRepository().getFullName());
            return false;
        }
        if (!config.valid()) {
            LOG.debug("Configuration file for '{}' is not valid",
                    payload.getRepository().getFullName());
            return false;
        }
        try {
            if (!properties.getAllowedOrganizations().contains(payload.getOrganization().getName())) {
                LOG.debug("Taking action for organization '{}' is not allowed",
                        payload.getOrganization().getName());
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
