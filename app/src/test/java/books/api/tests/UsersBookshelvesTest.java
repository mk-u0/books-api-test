package books.api.tests;

import books.api.config.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class UsersBookshelvesTest extends BaseTest {
    protected static final String VALID_USER_ID = "112556613386590764897";
    protected static final String INVALID_USER_ID = "not-a-real-user-id-123456789";
    protected static final String INVALID_SHELF_ID = "9999";

    @Test
    public void getUserBookshelves_validUser_returnsBookshelfList() {
        given().
            spec(publicSpec).
            pathParam("userId", VALID_USER_ID).
        when().
            get("/users/{userId}/bookshelves").
        then().
            statusCode(200).
            body("kind", equalTo("books#bookshelves")).
            body("items", not(empty()));
    }

    @Test
    public void getUserBookshelves_invalidUser_returnsError() {
        given().
            spec(publicSpec).
            pathParam("userId", INVALID_USER_ID).
        when().
            get("/users/{userId}/bookshelves").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    @Test
    public void getUserBookshelf_validShelf_returnsBookshelf() {
        given().
            spec(publicSpec).
            pathParam("userId", VALID_USER_ID).
            pathParam("shelf", BOOKSHELF_ID).
        when().
            get("/users/{userId}/bookshelves/{shelf}").
        then().
            statusCode(200).
            body("kind", equalTo("books#bookshelf")).
            body("id", equalTo(Integer.parseInt(BOOKSHELF_ID)));
    }

    @Test
    public void getUserBookshelf_invalidShelf_returnsNotFound() {
        given().
            spec(publicSpec).
            pathParam("userId", VALID_USER_ID).
            pathParam("shelf", INVALID_SHELF_ID).
        when().
            get("/users/{userId}/bookshelves/{shelf}").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    @Test
    public void getUserBookshelfVolumes_validShelf_returnsVolumes() {
        Response response =
        given().
            spec(publicSpec).
            pathParam("userId", VALID_USER_ID).
            pathParam("shelf", BOOKSHELF_ID).
        when().
            get("/users/{userId}/bookshelves/{shelf}/volumes").
        then().
            statusCode(200).
            body("kind", equalTo("books#volumes")).
            extract().response();

        response.then().body("totalItems", greaterThanOrEqualTo(0));
    }

    @Test
    public void getUserBookshelfVolumes_invalidShelf_returnsNotFound() {
        given().
            spec(publicSpec).
            pathParam("userId", VALID_USER_ID).
            pathParam("shelf", INVALID_SHELF_ID).
        when().
            get("/users/{userId}/bookshelves/{shelf}/volumes").
        then().
            statusCode(anyOf(is(400), is(404)));
    }
}
