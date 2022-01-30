package io.apicurio.bot.actions;

import io.apicurio.bot.cache.NotificationCache;
import io.apicurio.bot.config.ApicurioBotProperties;
import io.apicurio.bot.util.Templates;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GoogleChatNotify {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleChatNotify.class);

    @Inject
    NotificationCache ncache;

    @Inject
    ApicurioBotProperties properties;

    private static final HttpClient client;

    static {
        client = HttpClient.newHttpClient();
    }

    // At 07:00:00am, on every Monday, Tuesday, Wednesday, Thursday and Friday, every month
    // TODO Timezone?
    @Scheduled(cron = "0 0 7 ? * MON,TUE,WED,THU,FRI *")
    void onSchedule() {
        try {
            if (properties.getGoogleChatWebhookUrl().isPresent() && !ncache.isEmpty()) {
                var message = Templates.chatNotifyReview(ncache.getIssues(), ncache.getPullRequests()).render();

                if (!properties.isDryRun()) {
                    var url = properties.getGoogleChatWebhookUrl().get();
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(url))
                                .header("Content-Type", "application/json; charset=UTF-8")
                                .POST(HttpRequest.BodyPublishers.ofString(message))
                                .build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() >= 300) {
                            LOG.error("Got an error when sending Google webhook HTTP request. Code: {}, Response: {}",
                                    response.statusCode(), response.body());
                        }
                    } catch (URISyntaxException e) {
                        LOG.error("Google webhook URL '" + url + "' is invalid", e);
                    } catch (InterruptedException e) {
                        LOG.error("Thread was interrupted when sending an HTTP request", e);
                    } catch (IOException e) {
                        LOG.error("Could not send an HTTP request", e);
                    }
                } else {
                    LOG.info("Google chat - Send message: {}", message);
                }
            }
        } finally {
            // Make sure we do not run out of memory
            ncache.clear();
        }
    }
}

/*
 * {
 * "cards": [
 * {
 * "header": {
 * "title": "List of new GitHub issues and pull requests that need triage or review",
 * },
 * "sections": [
 * {
 * "header": "Issues",
 * "widgets": [
 * {
 * "keyValue": {
 * "topLabel": "Issue Title",
 * "content": "12345"
 * }
 * },
 * {
 * "keyValue": {
 * "topLabel": "Status",
 * "content": "In Delivery"
 * }
 * }
 * ]
 * },
 * ]
 * }
 * ]
 * }
 *
 *
 */