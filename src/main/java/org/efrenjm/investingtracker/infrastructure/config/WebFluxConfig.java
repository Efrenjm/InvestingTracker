package org.efrenjm.investingtracker.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.security.port.in.UserLookupUseCase;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebFluxConfig implements WebFluxConfigurer {
	private final UserLookupUseCase userLookupUseCase;

	@Override
	public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
		configurer.addCustomResolver(new AuthUserResolver(userLookupUseCase));
	}
}
