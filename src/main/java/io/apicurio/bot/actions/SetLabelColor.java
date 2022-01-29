package io.apicurio.bot.actions;

import io.apicurio.bot.util.Labels;
import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.config.ApicurioBotConfigFile;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SetLabelColor {

    private static final Logger LOG = LoggerFactory.getLogger(SetLabelColor.class);

    @Inject
    ApicurioBotProperties properties;

    public void handle(ApicurioBotConfigFile config, GHEventPayload.Label labelPayload) throws IOException {
        GHLabel label = labelPayload.getLabel();
        String targetColor = null;

        if (label.getName().startsWith(Labels.AREA_PREFIX)
                && !config.labels.areaLabelColor.equals(label.getColor().toLowerCase(Locale.ROOT))) {
            targetColor = config.labels.areaLabelColor;
        }

        if (label.getName().startsWith(Labels.TRIAGE_PREFIX)
                && !config.labels.triageLabelColor.equals(label.getColor().toLowerCase(Locale.ROOT))) {
            targetColor = config.labels.triageLabelColor;
        }

        if (targetColor != null) {
            if (!properties.isDryRun()) {
                label.set().color(targetColor);
            } else {
                LOG.info("Label {} - Set color: {}", label.getName(), targetColor);
            }
        }
    }
}
