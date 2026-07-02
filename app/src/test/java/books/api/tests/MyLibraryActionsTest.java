package books.api.tests;

import books.api.config.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class MyLibraryActionsTest extends BaseTest {

    protected static final String INVALID_VOLUME_ID = "not-a-real-volume-id";

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

    @Test(priority = 4)
    public void clearVolumes_validShelf_returnsOk() {
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
