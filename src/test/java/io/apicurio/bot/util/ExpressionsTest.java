package io.apicurio.bot.util;

import io.apicurio.bot.util.Expressions;
import io.quarkus.qute.TemplateException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ExpressionsTest {

    @Inject
    Expressions ex;

    @Test
    void test() {
        Map<String, Object> data = Map.of("title", "foo");
        assertTrue(ex.evaluateBoolean("title.startsWith(\"foo\")", data));
        try {
            assertFalse(ex.evaluateBoolean("config:property('quarkus.log.level') == 'DEBUG'", data));
            Assertions.fail("Template must not have access to Quarkus properties.");
        } catch (TemplateException ex) {
            // ok
        }
    }
}
