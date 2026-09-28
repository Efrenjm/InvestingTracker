package org.efrenjm.investingtracker.interfaces.web.advice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.efrenjm.investingtracker.domain.exception.BadRequestException;
import org.efrenjm.investingtracker.domain.exception.ConflictException;
import org.efrenjm.investingtracker.domain.exception.ResourceNotFoundException;
import org.efrenjm.investingtracker.interfaces.web.advice.problem.ApiProblem;
import org.efrenjm.investingtracker.interfaces.web.advice.problem.ApiProblemFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
@Order(2)
public class GlobalExceptionHandler {
    private final ApiProblemFactory problemFactory;

    public GlobalExceptionHandler(ApiProblemFactory problemFactory) {
        this.problemFactory = problemFactory;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiProblem> handleUnexpectedError(Exception ex) {
        return problemFactory.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Internal server error",
                "An unexpected error occurred");
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiProblem> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, List<String>> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .collect(
                                Collectors.groupingBy(
                                        error -> error.getField(),
                                        Collectors.mapping(
                                                error -> error.getDefaultMessage(),
                                                Collectors.toList())));
        return problemFactory.validation(errors);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiProblem> handleBadRequest(BadRequestException ex) {
        return problemFactory.create(
                HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Bad request", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiProblem> handleConflict(ConflictException ex) {
        return problemFactory.create(HttpStatus.CONFLICT, "CONFLICT", "Conflict", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiProblem> resourceNotFoundError(ResourceNotFoundException ex) {
        return problemFactory.create(
                HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Resource not found", ex.getMessage());
    }
}
