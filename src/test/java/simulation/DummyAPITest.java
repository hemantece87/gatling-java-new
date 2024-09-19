package simulation;

import static com.typesafe.config.ConfigSyntax.JSON;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static config.PerfTestConfigs.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import utils.DataProviderUtils;

import static utils.DataProviderUtils.*;

public class DummyAPITest extends Simulation {

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

    private static final FeederBuilder.FileBased<String> csvFeeder = csv("TestData/testdata.csv").circular();

    private static final ChainBuilder getAllItems =
            exec(http("Get First endpoint")
                            .get(GET_API_ENDPOINT)
                            .check(status().is(200))
                            .check(jmesPath("[? name=='Bert'].id").saveAs("id")));

    private static final ChainBuilder getSpecificItem =
            feed(csvFeeder)
                    .exec(http("Get specific resource with id")
                    .get(GET_API_ENDPOINT + "#{id}/")
                    .check(status().is(200)));

    private static final ChainBuilder register =
            exec(session -> {
                // Generate a random number
                String randomNumber = DataProviderUtils.getRandomNumber(6); // 4-digit random number
                return session.set("randomNumber", randomNumber);
            }).
                    exec(http("Register new user")
                    .post(REGISTER_USER_ENDPOINT)
                    .body(StringBody("{\n" +
                            "    \"username\": \"test_#{randomNumber}\",\n" +
                            "    \"password\": \"test\"\n" +
                            "}")));

    // Define the scenario
    ScenarioBuilder scenario = scenario("Test E2E flow")
            .forever().on(
             exec(register)
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
