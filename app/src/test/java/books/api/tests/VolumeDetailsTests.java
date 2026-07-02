package books.api.tests;

import books.api.config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Volume Details")
public class VolumeDetailsTests extends BaseTest {

    // -----------------------------------------------------------------------
    // Retrieve Volume Information (basic fields)
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve a volume by ID – checks id, title and authors are present")
    public void retrieveVolumeInformation() {
        given()
            .spec(publicSpec)
        .when()
            .get("/volumes/" + VALID_VOLUME_ID)
        .then()
            .statusCode(200)
            .body("id", not(emptyOrNullString()))
            .body("volumeInfo.title", not(emptyOrNullString()))
            .body("volumeInfo.authors", instanceOf(java.util.List.class));
    }

    // -----------------------------------------------------------------------
    // Retrieve book details – title AND publisher
    // -----------------------------------------------------------------------
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve book details by valid volume ID – checks title and publisher exist")
    public void retrieveBookDetailsWithValidVolumeId() {
        given()
            .spec(publicSpec)
        .when()
            .get("/volumes/" + VALID_VOLUME_ID)
        .then()
            .statusCode(200)
            .body("volumeInfo.title", notNullValue())
            .body("volumeInfo.publisher", notNullValue());
    }
}
