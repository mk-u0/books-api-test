package books.api.config;

import static io.restassured.RestAssured.*;

public final class Authenticator {
    private static Authenticator auth;
    private Authenticator() {}
    public static Authenticator getInstance() {
        if (auth == null) {
            auth = new Authenticator();
        }
        return auth;
    }

    private final String API_KEY = System.getenv("API_KEY");
    private final String REFRESH_TOKEN = System.getenv("REFRESH_TOKEN");
    private final String CLIENT_ID = System.getenv("CLIENT_ID");
    private final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");

    private final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    private String token = null;


    public String getToken() {
        if (token == null) {
            token = authenticate();
        }
        return token;
    }

    public String getApiKey() {
        return API_KEY;
    }

    private String authenticate() {
        String accessToken =
        given().
            formParam("client_id", CLIENT_ID).
            formParam("client_secret", CLIENT_SECRET).
            formParam("grant_type", "refresh_token").
            formParam("refresh_token", REFRESH_TOKEN).
        when().
            post(TOKEN_URL).
        then().
            extract().path("access_token");
        return accessToken;
    }
}
