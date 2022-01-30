package io.apicurio.bot.config;

import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApicurioBotProperties {

    @ConfigProperty(name = "apicurio-bot.dry-run")
    Optional<Boolean> dryRun;

    @ConfigProperty(name = "apicurio-bot.allowed-organizations")
    @Getter
    List<String> allowedOrganizations;

    @ConfigProperty(name = "apicurio-bot.google-chat-webhook-url")
    @Getter
    Optional<String> googleChatWebhookUrl;

    public boolean isDryRun() {
        return dryRun.orElse(false);
    }
}
