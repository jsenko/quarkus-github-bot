package io.apicurio.bot.util;

import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateException;
import io.quarkus.qute.ValueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Expressions {

    private static final Logger LOG = LoggerFactory.getLogger(Expressions.class);

    private static final Engine engine;

    static {
        // We can't use the Qute engine instance due to security -
        // though unlikely, an attacker could, for example, call a method on an arbitrary bean
        // using `inject:` namespace, or access a configuration property.
        // We'll create a isolated instance, and have to add custom value resolver instead of a template extension.
        engine = Engine.builder()
                .addDefaults()
                .addValueResolver(ValueResolver.builder()
                        .appliesTo(ctx -> ctx.getBase() instanceof String && ctx.getName().equals("matches"))
                        .resolveSync(
                                // e.g. title.matches("foo.*")
                                ctx -> Matcher.matches((String) ctx.getParams().get(0).getLiteral(), (String) ctx.getBase()))
                        .build())
                .addValueResolver(ValueResolver.builder()
                        .appliesTo(ctx -> ctx.getBase() instanceof String && ctx.getName().equals("startsWith"))
                        .resolveSync(
                                // e.g. title.startsWith("foo")
                                ctx -> ((String) ctx.getBase()).startsWith((String) ctx.getParams().get(0).getLiteral()))
                        .build())
                .build();
    }

    public boolean evaluateBoolean(String expression, Map<String, Object> data) {
        try {
            var t = "{#if " + expression + "}true{#else}false{/if}";
            LOG.debug("Expresion template: {}", t);
            String result = engine.parse(t).render(data);
            LOG.warn("Expression result: {}", result);
            return "true".equals(result);
        } catch (TemplateException ex) {
            LOG.error("Failed to parse template '{}' with data '{}'", ex);
            return false;
        }
    }
}
