package simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static config.PerfTestConfigs.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
public class DummyAPITest extends Simulation {

    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json");


    private static ChainBuilder getAllItems =
            exec(http("Get First endpoint")
                            .get(GET_API_ENDPOINT)
                            .check(status().is(200))
                            .check(jmesPath("[? name=='Bert'].id").saveAs("id")));

    private static ChainBuilder getSpecificItem =
            exec(http("Get specific resource")
                    .get(GET_API_ENDPOINT + "#id/")
                    .check(status().is(200)));


    // Define the scenario: 2 users hitting the GET endpoint
    ScenarioBuilder scenario = scenario("Test E2E flow")
            .exec(getAllItems)
            .pause(1,2)

            .repeat(2).on
                    (exec(getSpecificItem))
            .pause(1,2);

    {
        // Simulate 2 users and run the scenario for 10 seconds
        setUp(
                scenario.injectOpen(
                        constantUsersPerSec(10).during(20) // 2 users per second for 10 seconds
                )
        ).protocols(httpProtocol);
    }
}
