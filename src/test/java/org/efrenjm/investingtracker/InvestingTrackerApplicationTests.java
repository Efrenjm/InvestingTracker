package org.efrenjm.investingtracker;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test requires running infrastructure (MongoDB, Redis). " +
		"Run manually with docker-compose or use Testcontainers.")
class InvestingTrackerApplicationTests {

    @Test
    void contextLoads() {
    }

}
