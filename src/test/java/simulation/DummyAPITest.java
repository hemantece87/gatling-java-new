package simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static config.PerfTestConfigs.*;
import static utils.HeaderUtils.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
public class DummyAPITest extends Simulation {

    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl("https://test-api.k6.io/")
            .acceptHeader("application/json");

    // Define the scenario: 2 users hitting the GET endpoint
    ScenarioBuilder scenario = scenario("Get request")
            .exec(http("Get First endpoint")
                            .get("/public/crocodiles/")
                            .check(status().is(200))
                    .check(jmesPath("[? name=='Bert'].id").saveAs("id")))
            .pause(1,2)

            .exec(session -> {
                int id = session.getInt("id");
                System.out.println("/public/crocodiles/#{id}");
                return session;
            })

            .exec(http("Get specific resource")
                    .get("/public/crocodiles/#{id}")
                    .check(status().is(200)))

            .pause(1,2);


    {
        // Simulate 2 users and run the scenario for 10 seconds
        setUp(
                scenario.injectOpen(
                        constantUsersPerSec(2).during(2) // 2 users per second for 10 seconds
                )
        ).protocols(httpProtocol);
    }
}
