package simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static config.PerfTestConfigs.*;
import static transactions.LoginTransactions.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class APIPerformanceTest extends Simulation {

    private static final int START_USER_COUNT = Integer.parseInt(System.getProperty("START_USERS", "5"));
    private static final int RAMP_USER_COUNT = Integer.parseInt(System.getProperty("RAMP_USERS", "10"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "10"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION", "30"));


    @Override
    public void before() {
        System.out.printf("Starting test with %d users%n", START_USER_COUNT);
        System.out.printf("Ramping up to %d users over %d seconds%n", RAMP_USER_COUNT, RAMP_DURATION);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
    }

    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");


    ScenarioBuilder scenario = scenario("Test E2E flow")
            .forever().on(
             exec(registerNewUser)
            .pause(1)

            .exec(getAllItems)
            .pause(1,2)

            .exec(getSpecificItem)
            .pause(1,2));

    {
        // Simulate users
        setUp(
                scenario.injectOpen(
                        nothingFor(2),
                        atOnceUsers(START_USER_COUNT),
                        rampUsers(RAMP_USER_COUNT).during(RAMP_DURATION)
                ).protocols(httpProtocol)
        ).maxDuration(TEST_DURATION);
    }
}
