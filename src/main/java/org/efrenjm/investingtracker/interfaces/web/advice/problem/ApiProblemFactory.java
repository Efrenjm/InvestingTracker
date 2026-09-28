package org.efrenjm.investingtracker.interfaces.web.advice.problem;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiProblemFactory {

    public ResponseEntity<ApiProblem> create(
            HttpStatus status, String code, String title, String detail) {
        ApiProblem problem =
                new ApiProblem(problemType(code), title, status.value(), code, detail, Map.of());

        return response(status, problem);
    }

    public ResponseEntity<ApiProblem> validation(Map<String, List<String>> errors) {
        ApiProblem problem =
                new ApiProblem(
                        problemType("VALIDATION_ERROR"),
                        "Validation failed",
                        HttpStatus.BAD_REQUEST.value(),
                        "VALIDATION_ERROR",
                        "One or more fields are invalid",
                        errors);

        return response(HttpStatus.BAD_REQUEST, problem);
    }

    private static ResponseEntity<ApiProblem> response(HttpStatus status, ApiProblem problem) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    private static URI problemType(String code) {
        String path = code.toLowerCase().replace('_', '-');
        return URI.create("/problems/" + path);
    }
}
