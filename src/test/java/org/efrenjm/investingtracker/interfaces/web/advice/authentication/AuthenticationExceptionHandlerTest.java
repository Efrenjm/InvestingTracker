package org.efrenjm.investingtracker.interfaces.web.advice.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.efrenjm.investingtracker.application.service.authentication.exceptions.InvalidCredentialsException;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.UserAlreadyExistsException;
import org.efrenjm.investingtracker.interfaces.web.advice.problem.ApiProblemFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class AuthenticationExceptionHandlerTest {

    private final AuthenticationExceptionHandler handler =
            new AuthenticationExceptionHandler(new ApiProblemFactory());

    @Test
    void handleInvalidCredentialsReturnsProblemJsonWithStableCode() {
        var response = handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().code());
    }

    @Test
    void handleUserAlreadyExistsReturnsConflictProblemJson() {
        var response = handler.handleUserAlreadyExistsException(new UserAlreadyExistsException());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        assertEquals("USER_ALREADY_EXISTS", response.getBody().code());
        assertEquals(
                "You’re almost there! Check your inbox for the next steps.",
                response.getBody().detail());
    }
}
