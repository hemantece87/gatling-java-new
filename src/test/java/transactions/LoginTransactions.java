package transactions;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import utils.DataProviderUtils;

import static config.PerfTestConfigs.GET_API_ENDPOINT;
import static config.PerfTestConfigs.REGISTER_USER_ENDPOINT;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class LoginTransactions {
    private static final FeederBuilder.FileBased<String> csvFeeder = csv("TestData/testdata.csv").circular();
    public static final ChainBuilder getAllItems =
            exec(http("Get All items")
                    .get(GET_API_ENDPOINT)
                    .check(status().is(200)));

    public static final ChainBuilder getSpecificItem =
            feed(csvFeeder)
                    .exec(http("Get specific item with id")
                            .get(GET_API_ENDPOINT + "#{id}/")
                            .check(status().is(200)));

    public static final ChainBuilder registerNewUser =
            exec(session -> {
                String randomNumber = DataProviderUtils.getRandomNumber(6);
                return session.set("randomNumber", randomNumber);
            }).
                    exec(http("Register new user")
                            .post(REGISTER_USER_ENDPOINT)
                            .body(StringBody("{\n" +
                                    "    \"username\": \"test_#{randomNumber}\",\n" +
                                    "    \"password\": \"test\"\n" +
                                    "}")));
}
