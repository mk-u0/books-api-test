package books.api.tests;

import books.api.config.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class MyLibraryBookshelvesTest extends BaseTest {
    protected static final String INVALID_SHELF_ID = "9999";

    @Test
    public void getMyBookshelves_authenticated_returnsBookshelfList() {
        given().
            spec(authSpec).
        when().
            get("/mylibrary/bookshelves").
        then().
            statusCode(200).
            body("kind", equalTo("books#bookshelves")).
            body("items", not(empty()));
    }

    @Test
    public void getMyBookshelves_unauthenticated_returnsUnauthorized() {
        given().
            spec(publicSpec).
        when().
            get("/mylibrary/bookshelves").
        then().
            statusCode(anyOf(is(401), is(403)));
    }

    @Test
    public void getMyBookshelf_validShelf_returnsBookshelf() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
        when().
            get("/mylibrary/bookshelves/{shelf}").
        then().
            statusCode(200).
            body("kind", equalTo("books#bookshelf")).
            body("id", equalTo(Integer.parseInt(BOOKSHELF_ID)));
    }

    @Test
    public void getMyBookshelf_invalidShelf_returnsNotFound() {
        given().
            spec(authSpec).
            pathParam("shelf", INVALID_SHELF_ID).
        when().
            get("/mylibrary/bookshelves/{shelf}").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    @Test
    public void getMyBookshelfVolumes_validShelf_returnsVolumes() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
        when().
            get("/mylibrary/bookshelves/{shelf}/volumes").
        then().
            statusCode(200).
            body("kind", equalTo("books#volumes")).
            body("totalItems", greaterThanOrEqualTo(0));
    }

    @Test
    public void getMyBookshelfVolumes_invalidShelf_returnsNotFound() {
        given().
            spec(authSpec).
            pathParam("shelf", INVALID_SHELF_ID).
        when().
            get("/mylibrary/bookshelves/{shelf}/volumes").
        then().
            statusCode(anyOf(is(400), is(404)));
    }
}
