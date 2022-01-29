package io.apicurio.bot;

import io.apicurio.bot.config.ApicurioBotProperties;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class ApicurioBot {

    private static final Logger LOG = LoggerFactory.getLogger(ApicurioBot.class);

    @Inject
    ApicurioBotProperties properties;

    void init(@Observes StartupEvent startupEvent) {
        if (properties.isDryRun()) {
            LOG.warn("Apicurio Bot running in dry-run mode");
        }
    }
}
