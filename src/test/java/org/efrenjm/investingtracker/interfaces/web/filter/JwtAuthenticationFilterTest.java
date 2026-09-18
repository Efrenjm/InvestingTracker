package org.efrenjm.investingtracker.interfaces.web.filter;

import org.efrenjm.investingtracker.application.security.port.in.JwtAuthenticationUseCase;
import org.efrenjm.investingtracker.application.security.port.in.SecuritySessionUseCase;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
	@Mock
	private JwtAuthenticationUseCase jwtAuthenticationUseCase;
	@Mock
	private SecuritySessionUseCase sessionUseCase;
	@Mock
	private WebFilterChain chain;

	@Test
	void filter_DoesNotAuthenticateWhenValidTokenSessionIsInactive() {
		ServerWebExchange exchange = exchangeWithToken("signed-token");
		when(jwtAuthenticationUseCase.isValidToken("signed-token")).thenReturn(true);
		when(chain.filter(exchange)).thenReturn(Mono.empty());

		when(jwtAuthenticationUseCase.extractSessionId("signed-token")).thenReturn("session-1");
		when(sessionUseCase.isSessionActive("session-1")).thenReturn(Mono.just(false));
		StepVerifier.create(new JwtAuthenticationFilter(jwtAuthenticationUseCase, sessionUseCase).filter(exchange, chain))
				.verifyComplete();

		verify(chain).filter(exchange);
		org.junit.jupiter.api.Assertions.assertFalse(exchange.getAttributes().containsKey("authUser"));
	}

	@Test
	void filter_ForwardsRequestWhenValidTokenSessionIsActive() {
		ServerWebExchange exchange = exchangeWithToken("signed-token");
		when(jwtAuthenticationUseCase.isValidToken("signed-token")).thenReturn(true);
		when(jwtAuthenticationUseCase.extractUserId("signed-token")).thenReturn("user-1");
		when(jwtAuthenticationUseCase.extractRoles("signed-token")).thenReturn(Set.of(SystemRole.STANDARD));
		when(jwtAuthenticationUseCase.extractSessionId("signed-token")).thenReturn("session-1");
		when(sessionUseCase.isSessionActive("session-1")).thenReturn(Mono.just(true));
		when(chain.filter(exchange)).thenReturn(Mono.empty());

		StepVerifier.create(new JwtAuthenticationFilter(jwtAuthenticationUseCase, sessionUseCase).filter(exchange, chain))
				.verifyComplete();

		verify(chain).filter(exchange);
	}

	private ServerWebExchange exchangeWithToken(String token) {
		return MockServerWebExchange.from(
				MockServerHttpRequest.get("/private")
						.cookie(new HttpCookie("jwt", token))
						.build());
	}
}
