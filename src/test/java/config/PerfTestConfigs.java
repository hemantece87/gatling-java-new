package config;

public class PerfTestConfigs {

    public static final String BASE_URL =  "https://test-api.k6.io";
    public static final String GET_API_ENDPOINT = "/public/crocodiles/";
    public static final String REGISTER_USER_ENDPOINT = "/user/register/";
//    public static final double REQUEST_PER_SECOND = getAsDoubleOrElse("requestPerSecond", 10f);
//    public static final long DURATION_MIN = getAsIntOrElse("durationMin", 1);
//    public static final int P95_RESPONSE_TIME_MS = getAsIntOrElse("p95ResponseTimeMs", 1000);
}
