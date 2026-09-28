package org.efrenjm.investingtracker.interfaces.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import org.efrenjm.investingtracker.interfaces.web.advice.problem.ApiProblemFactory;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler(new ApiProblemFactory());

    @Test
    void handleValidationExceptionsReturnsProblemWithFieldErrors() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "password", "Password must be provided"));
        WebExchangeBindException exception =
                new WebExchangeBindException(mock(MethodParameter.class), bindingResult);

        var response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertEquals(
                Map.of("password", List.of("Password must be provided")),
                response.getBody().errors());
    }

    @Test
    void handleUnexpectedErrorReturnsGenericProblemWithoutInternalDetails() {
        var response =
                handler.handleUnexpectedError(new RuntimeException("internal database details"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().code());
        assertEquals("An unexpected error occurred", response.getBody().detail());
    }
}
