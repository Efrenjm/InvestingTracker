package org.efrenjm.investingtracker.infrastructure.config;

import lombok.NonNull;
import org.efrenjm.investingtracker.interfaces.annotations.AuthenticatedUser;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthenticatedUserResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticatedUser.class);
	}

	@Override
	@NonNull
	public Mono<Object> resolveArgument(@NonNull MethodParameter parameter, @NonNull BindingContext bindingContext, ServerWebExchange exchange) {
		Object user = exchange.getAttribute("authenticatedUser");
		if (user == null) {
			return Mono.empty();
		}

		if (!parameter.getParameterType().isInstance(user)) {
			return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Type mismatch for authenticated user"));
		}

		return Mono.just(user);
	}
}
