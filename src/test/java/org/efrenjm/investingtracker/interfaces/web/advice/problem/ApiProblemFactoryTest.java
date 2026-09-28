package org.efrenjm.investingtracker.interfaces.web.advice.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class ApiProblemFactoryTest {

    private final ApiProblemFactory factory = new ApiProblemFactory();

    @Test
    void createReturnsProblemResponseWithStableFields() {
        ResponseEntity<ApiProblem> response =
                factory.create(
                        HttpStatus.UNAUTHORIZED,
                        "INVALID_CREDENTIALS",
                        "Authentication failed",
                        "Invalid credentials");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().code());
        assertEquals("Authentication failed", response.getBody().title());
        assertEquals("Invalid credentials", response.getBody().detail());
        assertFalse(response.getBody().type().toString().isBlank());
    }

    @Test
    void validationPreservesFieldMessages() {
        ResponseEntity<ApiProblem> response =
                factory.validation(
                        Map.of(
                                "password",
                                List.of("Password must be provided", "Password is too short")));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertEquals(
                List.of("Password must be provided", "Password is too short"),
                response.getBody().errors().get("password"));
    }
}
