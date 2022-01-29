package io.apicurio.bot.util;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.util.Set;

@CheckedTemplate
public final class Templates {

    public static native TemplateInstance triageIssueWelcome(Set<String> reviewers);

    public static native TemplateInstance triagePRWelcome(Set<String> reviewers);

    private Templates() {
    }
}
