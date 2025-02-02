package org.efrenjm.investingtracker.config;

import com.mongodb.reactivestreams.client.MongoClient;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

	private final MongoClient mongoClient;

	@Value("${spring.data.mongodb.database}")
	private String databaseName;

	public MongoConfig(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Override
	@NonNull
	public MongoClient reactiveMongoClient() {
		return mongoClient;
	}

	@Override
	@NonNull
	protected String getDatabaseName() {
		return databaseName;
	}

	@Bean
	public ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory factory) {
		return new ReactiveMongoTransactionManager(factory);
	}
}
