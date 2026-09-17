package org.efrenjm.investingtracker.interfaces.web.advice.problem;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiProblem(
        URI type,
        String title,
        int status,
        String code,
        String detail,
        Map<String, List<String>> errors
) { }
