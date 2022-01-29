package io.apicurio.bot;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.apicurio.bot.config.ApicurioBotProperties;
import io.quarkus.runtime.StartupEvent;

public class ApicurioBot {

    private static final Logger LOG = Logger.getLogger(ApicurioBot.class);

    @Inject
    ApicurioBotProperties properties;

    void init(@Observes StartupEvent startupEvent) {
        if (properties.isDryRun()) {
            LOG.warn("Apicurio Bot running in dry-run mode");
        }
    }
}
