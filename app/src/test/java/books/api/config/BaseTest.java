package books.api.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.authentication.OAuth2Scheme;
import org.testng.annotations.BeforeSuite;


public class BaseTest {
    protected static final String BASE_URL = "https://www.googleapis.com/books/v1";
    protected static Authenticator auth = Authenticator.getInstance();

    protected static RequestSpecification publicSpec;
    protected static RequestSpecification authSpec;

    @BeforeSuite
    public void setupTestSuite() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        publicSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addHeader("x-goog-api-key", auth.getApiKey())
                .log(LogDetail.URI)
                .build();

        OAuth2Scheme authScheme = new OAuth2Scheme();
        authScheme.setAccessToken(auth.getToken());
        authSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAuth(authScheme)
                .log(LogDetail.URI)
                .build();
    }
}
