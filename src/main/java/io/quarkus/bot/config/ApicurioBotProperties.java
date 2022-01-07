package io.quarkus.bot.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApicurioBotProperties {

    @ConfigProperty(name = "apicurio-bot.dry-run")
    Optional<Boolean> dryRun;

    public boolean isDryRun() {
        return dryRun.orElse(false);
    }
}
