package books.api.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.oauth2;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeMethod;


public class BaseTest {
    protected static final String BASE_URL = "https://www.googleapis.com/books/v1";
    protected static Authenticator auth = Authenticator.getInstance();

    // Common Test Data
    protected static final String BOOKSHELF_ID   = "2";   // "To Read" shelf
    protected static final String INVALID_API_KEY = "INVALID_KEY_12345";
    protected static final String REMOVE_VOLUME_ID = "VCBeDwAAQBAJ";
    protected static final String VALID_VOLUME_ID = "zyTCAlFPjgYC";

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

        authSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAuth(oauth2(auth.getToken()))
                .log(LogDetail.URI)
                .build();
    }

    @BeforeMethod
    public void throttle() {
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
