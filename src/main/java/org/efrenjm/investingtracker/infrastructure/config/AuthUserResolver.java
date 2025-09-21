package org.efrenjm.investingtracker.infrastructure.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.SecurityPort;
import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUserResolver implements HandlerMethodArgumentResolver
{
	private final SecurityPort securityService;

	@Override
	public boolean supportsParameter(MethodParameter parameter)
	{
		return parameter.hasParameterAnnotation(AuthUser.class);
	}

	@Override
	@NonNull
	public Mono<Object> resolveArgument(@NonNull MethodParameter parameter,
	                                    @NonNull BindingContext bindingContext,
	                                    ServerWebExchange exchange)
	{
		UserIdentity user = exchange.getAttribute("authUser");
		if (user == null)
		{
			return Mono.empty();
		}

		Class<?> paramType = parameter.getParameterType();
		if (paramType.equals(UserIdentity.class))
		{
			return Mono.just(user);
		}
		else if (paramType.equals(Profile.class))
		{
			return getProfile(user.id()).cast(Object.class);
		}
		else if (paramType.equals(User.class))
		{
			return getUser(user.id()).cast(Object.class);
		}
		else
		{
			return Mono.error(new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Unsupported parameter type for @AuthUser: " + paramType.getSimpleName()
			));
		}
	}

	private Mono<Profile> getProfile(String userId)
	{
		return securityService.loadProfileByUserId(userId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Profile not found for user ID: " + userId
				)));
	}

	private Mono<User> getUser(String userId)
	{
		return securityService.loadUserByUserId(userId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"User not found for ID: " + userId
				)));
	}
}
