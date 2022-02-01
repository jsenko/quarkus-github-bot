package io.apicurio.bot.util;

import io.apicurio.bot.config.ApicurioBotConfigFile;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestFileDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TriageRules {

    private static final Logger LOG = LoggerFactory.getLogger(TriageRules.class);

    @Inject
    Expressions expressions;

    public boolean matchRuleByExpressions(GHIssue issue, ApicurioBotConfigFile.TriageRule rule) {

        // Expressions
        if (rule.expressions != null) {
            for (String expression : rule.expressions) {
                try {

                    Map<String, Object> data = new HashMap<>();
                    data.put("title", issue.getTitle());
                    data.put("body", issue.getBody());
                    return expressions.evaluateBoolean(expression, data);
                } catch (Exception ex) {
                    LOG.error("Error evaluating expression '" + expression + "'", ex);
                }
            }
        }
        return false;
    }

    public boolean matchRuleByPatterns(GHIssue issue, ApicurioBotConfigFile.TriageRule rule) {

        // Patterns
        if (rule.patterns != null) {

            Set<String> anywhere = new HashSet<>(rule.patterns.anywhere);
            Set<String> title = new HashSet<>(rule.patterns.title);
            title.addAll(anywhere);
            Set<String> body = new HashSet<>(rule.patterns.body);
            body.addAll(anywhere);

            for (String pattern : title) {
                if (Patterns.find(pattern, issue.getTitle())) {
                    return true;
                }
            }

            for (String pattern : body) {
                if (Patterns.find(pattern, issue.getBody())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean matchRuleByFiles(GHPullRequest pr, ApicurioBotConfigFile.TriageRule rule) {
        if (rule.patterns != null && rule.patterns.files != null) {

            Set<String> files = new HashSet<>(rule.patterns.files);
            files.addAll(rule.patterns.anywhere);

            for (GHPullRequestFileDetail file : pr.listFiles()) {
                var a = file.getPreviousFilename();
                var b = file.getFilename();
                for (String directory : files) {
                    if (Patterns.find(directory, a)) {
                        return true;
                    }
                    if (Patterns.find(directory, b)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
