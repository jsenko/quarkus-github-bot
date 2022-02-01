package io.apicurio.bot.util;

import io.quarkus.qute.TemplateException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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

        data = new HashMap<>();
        data.put("body", null);
        assertTrue(ex.evaluateBoolean("body.startsWith(\"test\")", data));
    }
}
