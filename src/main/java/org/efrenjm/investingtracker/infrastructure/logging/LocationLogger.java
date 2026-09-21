package org.efrenjm.investingtracker.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/** Preconfigured logger: callers supply only a static location and a safe message. */
public final class LocationLogger {
    private final Logger logger;
    private final String module;

    private LocationLogger(Class<?> source, String module) {
        if (module == null || !module.matches("[a-z][a-z0-9-]*")) {
            throw new IllegalArgumentException("A lowercase module name is required");
        }
        this.logger = LoggerFactory.getLogger(source);
        this.module = module;
    }

    public static LocationLogger forClass(Class<?> source, String module) {
        return new LocationLogger(source, module);
    }

    public void info(String location, String message) { write(Level.INFO, location, message); }
    public void warn(String location, String message) { write(Level.WARN, location, message); }
    public void error(String location, String message) { write(Level.ERROR, location, message); }
    public void debug(String location, String message) { write(Level.DEBUG, location, message); }

    private void write(Level level, String location, String message) {
        if (location == null || !location.matches("[a-z0-9]{9}")) {
            throw new IllegalArgumentException("A static nine-character lowercase alphanumeric location is required");
        }
        logger.atLevel(level).addKeyValue("module", module).addKeyValue("location", location).log(message);
    }
}
