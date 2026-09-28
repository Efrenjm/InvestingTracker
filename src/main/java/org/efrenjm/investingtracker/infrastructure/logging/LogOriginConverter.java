package org.efrenjm.investingtracker.infrastructure.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import ch.qos.logback.classic.spi.ILoggingEvent;

/** Formats scoped application logs while retaining the legacy logger column elsewhere. */
public class LogOriginConverter extends ClassicConverter {
    private final TargetLengthBasedClassNameAbbreviator legacy =
            new TargetLengthBasedClassNameAbbreviator(36);

    @Override
    public String convert(ILoggingEvent event) {
        String module = null;
        String location = null;
        if (event.getKeyValuePairs() != null) {
            for (var pair : event.getKeyValuePairs()) {
                if (pair.key.equals("module")) {
                    module = String.valueOf(pair.value);
                }
                if (pair.key.equals("location")) {
                    location = String.valueOf(pair.value);
                }
            }
        }
        if (module == null || location == null) {
            return legacy.abbreviate(event.getLoggerName());
        }
        String source = event.getLoggerName();
        return module + " | " + source.substring(source.lastIndexOf('.') + 1) + " | " + location;
    }
}
