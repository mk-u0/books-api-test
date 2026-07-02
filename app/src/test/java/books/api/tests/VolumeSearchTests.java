package books.api.tests;

import books.api.config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Volume Search")
public class VolumeSearchTests extends BaseTest {

    // -----------------------------------------------------------------------
    // Search books using a valid keyword
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Search books using a valid keyword – expects results to be returned")
    public void searchBooksWithValidKeyword() {
        given()
            .spec(publicSpec)
            .queryParam("q", "java")
        .when()
            .get("/volumes")
        .then()
            .statusCode(200)
            .body("totalItems", greaterThan(0))
            .body("items", notNullValue())
            .body("items", instanceOf(java.util.List.class));
    }

    // -----------------------------------------------------------------------
    // Search books by author
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Search books by author name using the 'inauthor' qualifier")
    public void searchBooksByAuthor() {
        given()
            .spec(publicSpec)
            .queryParam("q", "inauthor:Joshua Bloch")
        .when()
            .get("/volumes")
        .then()
            .statusCode(200)
            .body("items.size()", greaterThan(0));
    }

    // -----------------------------------------------------------------------
    // Search books using multiple keywords
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Search books using multiple keywords – expects totalItems > 0")
    public void searchBooksWithMultipleKeywords() {
        given()
            .spec(publicSpec)
            .queryParam("q", "java programming")
        .when()
            .get("/volumes")
        .then()
            .statusCode(200)
            .body("totalItems", greaterThan(0));
    }

    // -----------------------------------------------------------------------
    // Search books using an empty keyword (negative case)
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Search with an empty 'q' parameter – expects HTTP 400 and a missing-query error")
    public void searchBooksWithEmptyKeyword() {
        given()
            .spec(publicSpec)
            .queryParam("q", "")
        .when()
            .get("/volumes")
        .then()
            .statusCode(400)
            .body("error.message", containsStringIgnoringCase("Missing query"));
    }

    // -----------------------------------------------------------------------
    // Search books using an invalid API key (negative case)
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Search using an invalid API key – expects 400/401/403 and an error body")
    public void searchBooksWithInvalidApiKey() {
        given()
            .baseUri(BASE_URL)
            .queryParam("q", "java")
            .queryParam("key", INVALID_API_KEY)
        .when()
            .get("/volumes/" + VALID_VOLUME_ID)
        .then()
            .statusCode(anyOf(is(400), is(401), is(403)))
            .body("error", notNullValue());
    }

    // -----------------------------------------------------------------------
    // Search books with an invalid maxResults value (boundary / negative case)
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("maxResults=100 exceeds the API maximum of 40 – expects 200 or 400")
    public void searchBooksWithInvalidMaxResults() {
        Response response = given()
            .spec(publicSpec)
            .queryParam("q", "java")
            .queryParam("maxResults", 100)
        .when()
            .get("/volumes")
        .then()
            .extract().response();

        int status = response.statusCode();
        assert status == 200 || status == 400
                : "Expected 200 or 400 but got " + status;
    }

    // -----------------------------------------------------------------------
    // Search books using an invalid filter value (negative case)
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("Search with filter=invalidFilter – API may return 200 or 400; response body must exist")
    public void searchBooksWithInvalidFilterValue() {
        Response response = given()
            .spec(publicSpec)
            .queryParam("q", "java")
            .queryParam("filter", "invalidFilter")
        .when()
            .get("/volumes")
        .then()
            .body(notNullValue())
            .extract().response();

        int status = response.statusCode();
        assert status == 200 || status == 400
                : "Expected 200 or 400 but got " + status;
    }
}
