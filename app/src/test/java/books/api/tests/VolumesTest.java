package books.api.tests;

import books.api.config.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Covers:
 *  GET volumes/{volumeId}
 *  GET volumes?q={search terms}
 *
 * Public, read-only endpoints -> publicSpec (API key auth).
 */
public class VolumesTest extends BaseTest {

    protected static final String INVALID_VOLUME_ID = "not-a-real-volume-id";
    protected static final String SEARCH_TERM = "harry+potter";

    // ---------- volumes/{volumeId} ----------

    @Test
    public void getVolume_validVolumeId_returnsVolume() {
        given().
            spec(publicSpec).
            pathParam("volumeId", VALID_VOLUME_ID).
        when().
            get("/volumes/{volumeId}").
        then().
            statusCode(200).
            body("kind", equalTo("books#volume")).
            body("id", equalTo(VALID_VOLUME_ID));
    }

    @Test
    public void getVolume_invalidVolumeId_returnsNotFound() {
        given().
            spec(publicSpec).
            pathParam("volumeId", INVALID_VOLUME_ID).
        when().
            get("/volumes/{volumeId}").
        then().
            statusCode(anyOf(is(400), is(404)));
    }

    // ---------- volumes?q={search terms} ----------

    @Test
    public void searchVolumes_validQuery_returnsResults() {
        given().
            spec(publicSpec).
            queryParam("q", SEARCH_TERM).
        when().
            get("/volumes").
        then().
            statusCode(200).
            body("kind", equalTo("books#volumes")).
            body("totalItems", greaterThan(0));
    }

    @Test
    public void searchVolumes_emptyQuery_returnsBadRequest() {
        given().
            spec(publicSpec).
            queryParam("q", "").
        when().
            get("/volumes").
        then().
            statusCode(400);
    }
}
