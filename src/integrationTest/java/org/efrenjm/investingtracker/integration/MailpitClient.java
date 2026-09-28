package org.efrenjm.investingtracker.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP client used to interact with the Mailpit API in integration tests.
 *
 * <p>Mailpit exposes a REST API on port 8025 that allows tests to read emails sent by the
 * application, without requiring a real SMTP server.
 *
 * <p>API docs: https://mailpit.axllent.org/docs/api-v1/
 */
public class MailpitClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private final String baseUrl;

    public MailpitClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Waits until at least one email arrives for the target recipient and returns the plain-text
     * body of the latest message.
     *
     * @param toEmail recipient address used to filter messages
     * @param timeoutMs maximum wait time in milliseconds
     * @return plain-text email body
     */
    public String waitForEmailBody(String toEmail, long timeoutMs)
            throws IOException, InterruptedException {

        long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            String messageId = findLatestMessageId(toEmail);
            if (messageId != null) {
                return fetchMessageBody(messageId);
            }
            Thread.sleep(500);
        }
        throw new AssertionError(
                "No email received for " + toEmail + " within " + timeoutMs + "ms");
    }

    /** Extracts a 6-character alphanumeric verification code (A-Z, 0-9) from the email body. */
    public String extractVerificationCode(String emailBody) {
        Pattern pattern = Pattern.compile("\\b([A-Z0-9]{6})\\b");
        Matcher matcher = pattern.matcher(emailBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new AssertionError(
                "No 6-character alphanumeric verification code found in email body:\n" + emailBody);
    }

    /**
     * Deletes all emails currently stored in Mailpit. Useful to clear state between tests
     * (@BeforeEach).
     */
    public void deleteAllMessages() throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/api/v1/messages"))
                        .DELETE()
                        .build();
        HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }

    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private String findLatestMessageId(String toEmail) throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/api/v1/messages?limit=50"))
                        .GET()
                        .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = OBJECT_MAPPER.readTree(response.body());
        JsonNode messages = root.path("messages");

        if (!messages.isArray()) {
            return null;
        }

        for (JsonNode msg : messages) {
            JsonNode toArray = msg.path("To");
            if (toArray.isArray()) {
                for (JsonNode recipient : toArray) {
                    String address = recipient.path("Address").asText("");
                    if (address.equalsIgnoreCase(toEmail)) {
                        return msg.path("ID").asText(null);
                    }
                }
            }
        }
        return null;
    }

    private String fetchMessageBody(String messageId) throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/api/v1/message/" + messageId))
                        .GET()
                        .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = OBJECT_MAPPER.readTree(response.body());

        // Prefer plain text over HTML
        String text = root.path("Text").asText("");
        if (!text.isBlank()) {
            return text;
        }

        return root.path("HTML").asText("");
    }
}
