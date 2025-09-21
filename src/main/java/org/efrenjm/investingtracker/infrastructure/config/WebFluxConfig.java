package org.efrenjm.investingtracker.infrastructure.config;

import org.efrenjm.investingtracker.domain.ports.inbound.SecurityPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
	SecurityPort securityService;

	@Override
	public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
		configurer.addCustomResolver(new AuthUserResolver(securityService));
	}
}
