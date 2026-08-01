package org.efrenjm.investingtracker.integration;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Base class for all integration tests.
 * Automatically starts Docker containers for MongoDB, Redis, and Mailpit
 * using Testcontainers. It does not affect the development database.
 *
 * Cleans MongoDB and Redis before each test to guarantee isolation.
 *
 * Requirement: Docker must be running.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class IntegrationTestBase {

    @Container
    static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
                    .withStartupTimeout(Duration.ofMinutes(2));

    @Container
    static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.2-alpine"))
                    .withStartupTimeout(Duration.ofMinutes(2));

    /**
     * Mailpit: fake SMTP server with an HTTP API to read test emails.
     * Port 1025 -> SMTP (receives emails from the app)
     * Port 8025 -> HTTP API (used by tests to read emails)
     */
    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> mailpitContainer =
            new GenericContainer<>(DockerImageName.parse("axllent/mailpit:v1.15.1"))
                    .withExposedPorts(1025, 8025)
                    .withStartupTimeout(Duration.ofMinutes(2))
                    .waitingFor(Wait.forListeningPort()); // Faster wait strategy than HTTP check

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private ReactiveRedisConnectionFactory redisConnectionFactory;

    /**
     * Cleans all MongoDB collections and all Redis keys
     * before each test to guarantee full test isolation.
     */
    @BeforeEach
    void cleanDatabase() {
        mongoTemplate.getCollectionNames()
                .flatMap(mongoTemplate::dropCollection)
                .blockLast();

        redisConnectionFactory.getReactiveConnection()
                .serverCommands()
                .flushAll()
                .block();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // MongoDB
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.ssl.enabled", () -> "false");

        // Redis
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("spring.data.redis.username", () -> "");
        registry.add("spring.data.redis.password", () -> "");

        // Mail (Mailpit SMTP)
        registry.add("spring.mail.host", mailpitContainer::getHost);
        registry.add("spring.mail.port", () -> mailpitContainer.getMappedPort(1025));
        registry.add("spring.mail.username", () -> "");
        registry.add("spring.mail.password", () -> "");
    }

    /**
     * Returns the base URL of the Mailpit HTTP API used to read emails.
     */
    protected static String getMailpitApiUrl() {
        return "http://" + mailpitContainer.getHost() + ":" + mailpitContainer.getMappedPort(8025);
    }
}

