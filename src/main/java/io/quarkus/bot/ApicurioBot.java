package io.quarkus.bot;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkus.bot.config.ApicurioBotProperties;
import io.quarkus.runtime.StartupEvent;

public class ApicurioBot {

    private static final Logger LOG = Logger.getLogger(ApicurioBot.class);

    @Inject
    ApicurioBotProperties quarkusBotConfig;

    void init(@Observes StartupEvent startupEvent) {
        if (quarkusBotConfig.isDryRun()) {
            LOG.warn("Apicurio Bot running in dry-run mode");
        }
    }
}
