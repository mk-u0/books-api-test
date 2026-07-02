package books.api.tests;

import org.testng.annotations.*;
import static io.restassured.RestAssured.*;
import io.restassured.builder.*;
import io.restassured.specification.*;
import static org.hamcrest.Matchers.*;
import books.api.auth.Authenticator;

public class ApiTests {
    protected final String volumeId = "qqeX8MJurLkC";
    protected final String title = "Critique of Pure Reason";

    protected Authenticator auth = new Authenticator();
    protected String accessToken = auth.getToken();
    protected final String key = auth.getApiKey();
    protected final String BASE_URI = "https://www.googleapis.com/books/v1/";

    RequestSpecification requestSpec;

    @BeforeClass
    public void buildRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri(BASE_URI);
        requestSpec = builder.build();
    }

    @Test
    public void getVolume() {
        given().
            spec(requestSpec).
            header("x-goog-api-key", key).
        when().
            get("/volumes/" + volumeId).
        then().
            statusCode(200).
            body("volumeInfo.title", equalTo(title));
    }

    @Test
    public void listVolumes() {
        given().
            auth().oauth2(accessToken).
            baseUri("https://www.googleapis.com/books/v1/").
        when().
            get("/mylibrary/bookshelves/2/volumes").
        then().
            statusCode(200).
            body("totalItems", equalTo(0));
    }
}
