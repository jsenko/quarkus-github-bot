package io.quarkus.bot;

import io.quarkiverse.githubapp.event.Issue;
import io.quarkiverse.githubapp.event.PullRequest;
import io.quarkus.bot.config.QuarkusBotConfig;
import io.quarkus.bot.util.GHIssues;
import io.quarkus.bot.util.GHPullRequests;
import io.quarkus.bot.util.Labels;
import org.jboss.logging.Logger;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

import javax.inject.Inject;
import java.io.IOException;

class RemoveInvalidLabelOnReopenAction {
    private static final Logger LOG = Logger.getLogger(RemoveInvalidLabelOnReopenAction.class);

    @Inject
    QuarkusBotConfig quarkusBotConfig;

    public void onIssueReopen(@Issue.Reopened GHEventPayload.Issue issuePayload) throws IOException {
        GHIssue issue = issuePayload.getIssue();

        if (GHIssues.hasLabel(issue, Labels.TRIAGE_INVALID)) {
            if (!quarkusBotConfig.isDryRun()) {
                issue.removeLabel(Labels.TRIAGE_INVALID);
            } else {
                LOG.info("Issue #" + issue.getNumber() + " - Remove label: " + Labels.TRIAGE_INVALID);
            }
        }
    }

    public void onPullRequestReopen(@PullRequest.Reopened GHEventPayload.PullRequest pullRequestPayload) throws IOException {
        GHPullRequest pullRequest = pullRequestPayload.getPullRequest();

        if (GHPullRequests.hasLabel(pullRequest, Labels.TRIAGE_INVALID)) {
            if (!quarkusBotConfig.isDryRun()) {
                pullRequest.removeLabel(Labels.TRIAGE_INVALID);
            } else {
                LOG.info("Pull request #" + pullRequest.getNumber() + " - Remove label: " + Labels.TRIAGE_INVALID);
            }
        }
    }

}
