package io.apicurio.bot.cache;

import io.vertx.core.impl.ConcurrentHashSet;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationCache {

    @Getter
    private final ConcurrentHashSet<IssueNotification> issues = new ConcurrentHashSet<>();

    @Getter
    private final ConcurrentHashSet<PullRequestNotification> pullRequests = new ConcurrentHashSet<>();

    public boolean isEmpty() {
        return issues.isEmpty() || pullRequests.isEmpty();
    }

    public void clear() {
        issues.clear();
        pullRequests.clear();
    }
}
