package org.efrenjm.investingtracker.infrastructure.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import net.logstash.logback.encoder.LogstashEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class LocationLoggerTest {
    @Test
    void formatsModuleClassAndStaticLocationWithoutRepeatedLabels() {
        Logger underlying = (Logger) LoggerFactory.getLogger(LocationLoggerTest.class);
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.start();
        underlying.addAppender(events);
        try {
            LocationLogger logger = LocationLogger.forClass(LocationLoggerTest.class, "auth");
            logger.info("a7k2m9x4p", "Verification email requested");
            logger.info("a7k2m9x4p", "Verification email requested");

            PatternLayout layout = new PatternLayout();
            layout.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
            layout.getInstanceConverterMap().put("origin", LogOriginConverter.class.getName());
            layout.setPattern("%level | %origin{36} | %msg%n");
            layout.start();
            assertEquals(2, events.list.size());
            for (ILoggingEvent event : events.list) {
                assertEquals("INFO | auth | LocationLoggerTest | a7k2m9x4p | Verification email requested"
                        + System.lineSeparator(), layout.doLayout(event));
                assertEquals(LocationLoggerTest.class.getName(), event.getLoggerName());
            }
            LogstashEncoder jsonEncoder = new LogstashEncoder();
            jsonEncoder.setContext(underlying.getLoggerContext());
            jsonEncoder.start();
            try {
                var json = new ObjectMapper().readTree(jsonEncoder.encode(events.list.getFirst()));
                assertEquals("auth", json.path("module").asText());
                assertEquals("a7k2m9x4p", json.path("location").asText());
                assertEquals("Verification email requested", json.path("message").asText());
            } catch (java.io.IOException error) {
                fail("Expected valid structured log JSON", error);
            } finally {
                jsonEncoder.stop();
            }
            layout.stop();
        } finally {
            underlying.detachAppender(events);
            events.stop();
        }
    }

    @Test
    void rejectsInvalidLocationCodes() {
        LocationLogger logger = LocationLogger.forClass(LocationLoggerTest.class, "auth");
        for (String invalid : new String[]{"AUTH-001", "ABC123XYZ", "short", "1234567890"}) {
            assertThrows(IllegalArgumentException.class, () -> logger.info(invalid, "Message"));
        }
    }

    @Test
    void leavesLegacyLoggerOutputUnchanged() {
        Logger underlying = (Logger) LoggerFactory.getLogger("org.efrenjm.investingtracker.example.LegacyService");
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.start();
        underlying.addAppender(events);
        try {
            underlying.info("Legacy message");
            PatternLayout original = new PatternLayout();
            original.setContext(underlying.getLoggerContext());
            original.setPattern("%logger{36}");
            original.start();
            assertEquals(original.doLayout(events.list.getFirst()),
                    new LogOriginConverter().convert(events.list.getFirst()));
            original.stop();
        } finally {
            underlying.detachAppender(events);
            events.stop();
        }
    }
}
