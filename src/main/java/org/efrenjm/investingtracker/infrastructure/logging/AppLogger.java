package org.efrenjm.investingtracker.infrastructure.logging;

import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Structured logging utility for consistent, searchable application logs.
 * <p>
 * Every log entry includes three structured fields:
 * <ul>
 *   <li><b>locId</b> — A unique location identifier for fast code searching (e.g. "AUTH-001", "WAL-012").</li>
 *   <li><b>status</b> — The event outcome: SUCCESS, FAIL, INFO, or WARN.</li>
 *   <li><b>method</b> — The method or operation name where the event occurred.</li>
 * </ul>
 * <p>
 * <b>Dev output</b> (human-readable text via logback pattern):
 * <pre>
 * 2026-08-01 11:24:54.123 | INFO  | a.s.a.AuthenticationService | [AUTH-001] [SUCCESS] [register] User created successfully
 * </pre>
 * <p>
 * <b>Prod output</b> (structured JSON via LogstashEncoder — fields are extracted as top-level keys):
 * <pre>
 * {"timestamp":"2026-08-01T11:24:54.123","level":"INFO","logger_name":"...AuthenticationService",
 *  "message":"[AUTH-001] [SUCCESS] [register] User created successfully",
 *  "locId":"AUTH-001","status":"SUCCESS","method":"register"}
 * </pre>
 * <p>
 * <b>Usage</b> (combine with Lombok's {@code @Slf4j}):
 * <pre>
 * {@literal @}Slf4j
 * public class AuthenticationService {
 *     public Mono&lt;User&gt; register(String username, String password) {
 *         return userRepository.save(user)
 *             .doOnSuccess(u -> AppLogger.success(log, "AUTH-001", "register", "User created: " + u.getId()))
 *             .doOnError(e -> AppLogger.fail(log, "AUTH-002", "register", "Registration failed", e));
 *     }
 * }
 * </pre>
 */
public final class AppLogger {

    private AppLogger() {
        // Utility class — prevent instantiation
    }

    // ─── Public API ──────────────────────────────────────────────

    /**
     * Logs a successful operation at INFO level.
     */
    public static void success(Logger log, String locId, String method, String message) {
        Marker marker = buildMarker(locId, "SUCCESS", method);
        log.info(marker, "[{}] [SUCCESS] [{}] {}", locId, method, message);
    }

    /**
     * Logs a failed operation at ERROR level.
     */
    public static void fail(Logger log, String locId, String method, String message) {
        Marker marker = buildMarker(locId, "FAIL", method);
        log.error(marker, "[{}] [FAIL] [{}] {}", locId, method, message);
    }

    /**
     * Logs a failed operation at ERROR level with the causing exception.
     */
    public static void fail(Logger log, String locId, String method, String message, Throwable throwable) {
        Marker marker = buildMarker(locId, "FAIL", method);
        log.error(marker, "[{}] [FAIL] [{}] {} — {}", locId, method, message, throwable.getMessage(), throwable);
    }

    /**
     * Logs an informational event at INFO level.
     */
    public static void info(Logger log, String locId, String method, String message) {
        Marker marker = buildMarker(locId, "INFO", method);
        log.info(marker, "[{}] [INFO] [{}] {}", locId, method, message);
    }

    /**
     * Logs a warning at WARN level.
     */
    public static void warn(Logger log, String locId, String method, String message) {
        Marker marker = buildMarker(locId, "WARN", method);
        log.warn(marker, "[{}] [WARN] [{}] {}", locId, method, message);
    }

    /**
     * Logs a debug-level event (only visible when logger is at DEBUG).
     */
    public static void debug(Logger log, String locId, String method, String message) {
        if (log.isDebugEnabled()) {
            Marker marker = buildMarker(locId, "DEBUG", method);
            log.debug(marker, "[{}] [DEBUG] [{}] {}", locId, method, message);
        }
    }

    // ─── Internal ────────────────────────────────────────────────

    /**
     * Builds a composite Marker with structured key-value pairs.
     * In text mode these are ignored; in JSON mode (LogstashEncoder)
     * they become top-level fields in the JSON output.
     */
    private static Marker buildMarker(String locId, String status, String method) {
        return Markers.aggregate(
                Markers.append("locId", locId),
                Markers.append("status", status),
                Markers.append("method", method)
        );
    }
}
