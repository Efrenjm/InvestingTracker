package org.efrenjm.investingtracker.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.cors")
public class CorsProperties {
	private boolean enabled = false;
	private String pathPattern = "/**";
	private List<String> allowedOrigins;
	private List<String> allowedMethods;
	private List<String> allowedHeaders;
	private List<String> exposedHeaders;
	private boolean allowCredentials = true;
	private Long maxAgeSeconds = 1800L;
}


