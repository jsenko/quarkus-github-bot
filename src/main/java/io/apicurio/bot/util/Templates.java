package io.apicurio.bot.util;

import io.apicurio.bot.cache.IssueNotification;
import io.apicurio.bot.cache.PullRequestNotification;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.util.Set;

@CheckedTemplate
public final class Templates {

    public static native TemplateInstance triageIssueWelcome(Set<String> reviewers);

    public static native TemplateInstance triagePRWelcome(Set<String> reviewers);

    public static native TemplateInstance chatNotifyReview(Set<IssueNotification> issues, Set<PullRequestNotification> prs);

    private Templates() {
    }
}
