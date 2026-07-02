package books.api.tests;

import books.api.config.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Covers the mutating mylibrary endpoints:
 *  POST mylibrary/bookshelves/{shelf}/addVolume
 *  POST mylibrary/bookshelves/{shelf}/clearVolumes
 *  POST mylibrary/bookshelves/{shelf}/moveVolume
 *  POST mylibrary/bookshelves/{shelf}/removeVolume
 *
 * All operate on the authenticated user's own library -> authSpec (OAuth2).
 *
 * Per the Google Books API documentation, these action endpoints respond
 * with 200 OK on success (not 204 No Content).
 *
 * Tests are ordered with TestNG's priority attribute since these calls mutate
 * shared shelf state (add must happen before move/remove, clearVolumes runs last).
 */
public class MyLibraryActionsTest extends BaseTest {

    protected static final String INVALID_VOLUME_ID = "not-a-real-volume-id";

    // ---------- addVolume ----------

    @Test(priority = 1)
    public void addVolume_validVolumeId_returnsOk() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", VALID_VOLUME_ID).
        when().
            post("/mylibrary/bookshelves/{shelf}/addVolume").
        then().
            statusCode(200);
    }

    @Test(priority = 1)
    public void addVolume_invalidVolumeId_returnsBadRequest() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", INVALID_VOLUME_ID).
        when().
            post("/mylibrary/bookshelves/{shelf}/addVolume").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    // ---------- moveVolume ----------
    // Depends on addVolume having placed VALID_VOLUME_ID on the shelf first.

    @Test(priority = 2, dependsOnMethods = "addVolume_validVolumeId_returnsOk")
    public void moveVolume_validVolumeId_returnsOk() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", VALID_VOLUME_ID).
            queryParam("volumePosition", 0).
        when().
            post("/mylibrary/bookshelves/{shelf}/moveVolume").
        then().
            statusCode(200);
    }

    @Test(priority = 2)
    public void moveVolume_invalidVolumeId_returnsBadRequest() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", INVALID_VOLUME_ID).
            queryParam("volumePosition", 0).
        when().
            post("/mylibrary/bookshelves/{shelf}/moveVolume").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    // ---------- removeVolume ----------
    // Depends on addVolume having placed VALID_VOLUME_ID on the shelf first.

    @Test(priority = 3, dependsOnMethods = "addVolume_validVolumeId_returnsOk")
    public void removeVolume_validVolumeId_returnsOk() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", VALID_VOLUME_ID).
        when().
            post("/mylibrary/bookshelves/{shelf}/removeVolume").
        then().
            statusCode(200);
    }

    @Test(priority = 3)
    public void removeVolume_invalidVolumeId_returnsBadRequest() {
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", INVALID_VOLUME_ID).
        when().
            post("/mylibrary/bookshelves/{shelf}/removeVolume").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    // ---------- clearVolumes ----------
    // Runs last since it wipes the entire shelf.

    @Test(priority = 4)
    public void clearVolumes_validShelf_returnsOk() {
        // Ensure the shelf has at least one volume so clearing is meaningful.
        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
            queryParam("volumeId", VALID_VOLUME_ID).
        when().
            post("/mylibrary/bookshelves/{shelf}/addVolume").
        then().
            statusCode(200);

        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
        when().
            post("/mylibrary/bookshelves/{shelf}/clearVolumes").
        then().
            statusCode(200);

        given().
            spec(authSpec).
            pathParam("shelf", BOOKSHELF_ID).
        when().
            get("/mylibrary/bookshelves/{shelf}/volumes").
        then().
            statusCode(anyOf(is(200), is(204))).
            body("totalItems", anyOf(equalTo(0), nullValue()));
    }

    @Test(priority = 4)
    public void clearVolumes_invalidShelf_returnsNotFound() {
        given().
            spec(authSpec).
            pathParam("shelf", "9999").
        when().
            post("/mylibrary/bookshelves/{shelf}/clearVolumes").
        then().
            statusCode(anyOf(is(400), is(404)));
    }
}
