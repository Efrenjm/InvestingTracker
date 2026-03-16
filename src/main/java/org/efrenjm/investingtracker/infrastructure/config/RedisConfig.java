package org.efrenjm.investingtracker.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.efrenjm.investingtracker.infrastructure.persistence.redis.UserSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig
{
	@Bean
	public ReactiveRedisTemplate<String, UserSession> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory factory,
			ObjectMapper objectMapper
	)
	{
		Jackson2JsonRedisSerializer<UserSession> serializer =
				new Jackson2JsonRedisSerializer<>(objectMapper, UserSession.class);

		RedisSerializationContext<String, UserSession> context =
				RedisSerializationContext.<String, UserSession>newSerializationContext()
						.key(StringRedisSerializer.UTF_8)
						.value(serializer)
						.hashKey(StringRedisSerializer.UTF_8)
						.hashValue(serializer)
						.build();

		return new ReactiveRedisTemplate<>(factory, context);
	}
}
