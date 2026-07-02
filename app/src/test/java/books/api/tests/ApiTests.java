package books.api.tests;

import org.testng.annotations.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import books.api.config.BaseTest;

public class ApiTests extends BaseTest {
    protected final String volumeId = "qqeX8MJurLkC";
    protected final String title = "Critique of Pure Reason";

    @Test
    public void getVolume() {
        given().
            spec(publicSpec).
        when().
            get("/volumes/" + volumeId).
        then().
            statusCode(200).
            body("volumeInfo.title", equalTo(title));
    }

    @Test
    public void listVolumes() {
        given().
            spec(authSpec).
        when().
            get("/mylibrary/bookshelves/2/volumes").
        then().
            statusCode(200).
            body("totalItems", equalTo(0));
    }
}
