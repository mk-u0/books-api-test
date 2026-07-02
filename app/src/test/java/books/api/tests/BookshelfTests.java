package books.api.tests;

import books.api.config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests for the My Library / bookshelf endpoints.
 *
 * These endpoints require a valid OAuth2 bearer token with the
 * "https://www.googleapis.com/auth/books" scope.
 * Set the GOOGLE_ACCESS_TOKEN environment variable before running.
 *
 * Test execution order matters for stateful tests:
 *   1. removeVolumeFromToReadBookshelf   (clean slate)
 *   2. listVolumesInEmptyToReadBookshelf (assert empty)
 *   3. addVolumeToToReadBookshelf        (add one)
 *   4. retrieveBooksFromInvalidBookshelf (error path – independent)
 *   5. addVolumeWithoutVolumeId          (error path – independent)
 */
@Feature("My Library – Bookshelf Management")
public class BookshelfTests extends BaseTest {
    private static final String SHELF_PATH = "/mylibrary/bookshelves/" + BOOKSHELF_ID;

    // -----------------------------------------------------------------------
    // 1.  Remove a volume (sets up clean state)
    // -----------------------------------------------------------------------
    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Remove a specific volume from the 'To Read' bookshelf – expects HTTP 200")
    public void removeVolumeFromToReadBookshelf() {
        given()
            .spec(authSpec)
            .queryParam("volumeId", REMOVE_VOLUME_ID)
        .when()
            .post(SHELF_PATH + "/removeVolume")
        .then()
            .statusCode(200);
    }

    // -----------------------------------------------------------------------
    // 2.  List volumes – expect shelf is empty
    // -----------------------------------------------------------------------
    @Test(priority = 2, dependsOnMethods = "removeVolumeFromToReadBookshelf")
    @Severity(SeverityLevel.NORMAL)
    @Description("List volumes in the 'To Read' bookshelf after removal – expects empty list")
    public void listVolumesInEmptyToReadBookshelf() {
        given()
            .spec(authSpec)
        .when()
            .get(SHELF_PATH + "/volumes")
        .then()
            .statusCode(200)
            // When the shelf is empty the API omits "items" entirely; treat null as []
            .body("totalItems", anyOf(nullValue(), is(0)));
    }

    // -----------------------------------------------------------------------
    // 3.  Add a volume
    // -----------------------------------------------------------------------
    @Test(priority = 3, dependsOnMethods = "listVolumesInEmptyToReadBookshelf")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add a valid volume to the 'To Read' bookshelf – expects HTTP 200")
    public void addVolumeToToReadBookshelf() {
        given()
            .spec(authSpec)
            .queryParam("volumeId", VALID_VOLUME_ID)
        .when()
            .post(SHELF_PATH + "/addVolume")
        .then()
            .statusCode(200);
    }

    // -----------------------------------------------------------------------
    // 4.  Retrieve books from an invalid bookshelf ID (negative case)
    // -----------------------------------------------------------------------
    @Test(priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Retrieve volumes from a non-existent bookshelf – expects 400 or 404 with error body")
    public void retrieveBooksFromInvalidBookshelf() {
        given()
            .spec(authSpec)
        .when()
            .get("/mylibrary/bookshelves/999999/volumes")
        .then()
            .statusCode(anyOf(is(400), is(404)))
            .body("error", notNullValue());
    }

    // -----------------------------------------------------------------------
    // 5.  Add volume without providing a volumeId (negative case)
    // -----------------------------------------------------------------------
    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempt to add a volume without supplying volumeId – expects HTTP 400 and error body")
    public void addVolumeWithoutVolumeId() {
        given()
            .spec(authSpec)
        .when()
            .post(SHELF_PATH + "/addVolume")
        .then()
            .statusCode(400)
            .body("error", notNullValue());
    }
}
