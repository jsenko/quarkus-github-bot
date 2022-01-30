package io.apicurio.bot.actions;

import io.apicurio.bot.cache.NotificationCache;
import io.apicurio.bot.util.Templates;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GoogleChatNotify {

    @Inject
    NotificationCache ncache;

    void onSchedule() {

        if (!ncache.isEmpty()) {
            var message = Templates.chatNotifyReview(ncache.getIssues(), ncache.getPullRequests()).render();
            // TODO send
        }
        ncache.clear();
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