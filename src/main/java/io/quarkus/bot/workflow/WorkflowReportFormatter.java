package io.quarkus.bot.workflow;

import javax.enterprise.context.ApplicationScoped;

import org.kohsuke.github.GHCheckRun;
import org.kohsuke.github.GHPullRequest;

import io.quarkus.bot.workflow.report.WorkflowReport;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@ApplicationScoped
public class WorkflowReportFormatter {

    public String getCheckRunReportSummary(WorkflowReport report, GHPullRequest pullRequest, boolean artifactsAvailable) {
        return Templates.checkRunReportSummary(report, pullRequest, artifactsAvailable).render();
    }

    public String getCheckRunReport(WorkflowReport report, boolean includeStackTraces) {
        return Templates.checkRunReport(report, includeStackTraces).render();
    }

    public String getCommentReport(WorkflowReport report, boolean artifactsAvailable, GHCheckRun checkRun,
            String messageIdActive, boolean includeStackTraces) {
        return Templates.commentReport(report, artifactsAvailable, checkRun, messageIdActive, includeStackTraces).render();
    }

    @CheckedTemplate
    private static class Templates {

        public static native TemplateInstance checkRunReportSummary(WorkflowReport report, GHPullRequest pullRequest,
                boolean artifactsAvailable);

        public static native TemplateInstance checkRunReport(WorkflowReport report, boolean includeStackTraces);

        public static native TemplateInstance commentReport(WorkflowReport report, boolean artifactsAvailable,
                GHCheckRun checkRun, String messageIdActive, boolean includeStackTraces);
    }
}
