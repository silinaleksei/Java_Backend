package lesson3;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpoonacularTest extends AbstractTest {

    @BeforeAll
    static void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void searchRecipesWithoutParamsTest() {
        given()
                //.queryParam("apiKey",getApiKey())
                .params("apiKey", getApiKey())
                //.log().all()
                .when()
                .get(getBaseUrl() + "recipes/complexSearch")
                .prettyPeek()
                .then()
                .statusCode(200)
                .assertThat()
                .body("number", equalTo(10))
                .body("offset", equalTo(0));
    }

    @Test
    public void searchRecipesItalianVegetarianMainCurseTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .params("cuisine", "italian")
                .params("diet", "vegetarian")
                .params("type", "main course")
                .params("number", 10)
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .statusCode(200)
                .assertThat()
                .body("number", equalTo(10))
                .body("offset", equalTo(0));
    }

    @Test
    public void searchRecipesNumber_100Test() {
        given()
                .queryParam("apiKey", getApiKey())
                .params("cuisine", "italian")
                .params("number", 100)
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .statusCode(200)
                .assertThat()
                .body("number", equalTo(100));
    }

    @Test
    public void searchRecipesCalories_500_800Test() {
        JsonPath response = given()
                .queryParam("apiKey", getApiKey())
                .params("cuisine", "italian")
                .params("number", 100)
                .params("minCalories", 500)
                .params("maxCalories", 800)
                .expect()
                .statusCode(200)
                .when()
                .get(getBaseUrl() + "recipes/complexSearch")
                .body()
                .jsonPath();
        assertThat(response.get("results[0].nutrition.nutrients[0].name"), equalTo("Calories"));
        assertThat(response.get("results[0].nutrition.nutrients[0].amount"), greaterThanOrEqualTo(500F));
        assertThat(response.get("results[0].nutrition.nutrients[0].amount"), lessThanOrEqualTo(800F));
    }

    @Test
    public void searchRecipesFat_20_100Test() {
        JsonPath response = given()
                .queryParam("apiKey", getApiKey())
                .params("cuisine", "italian")
                .params("number", 100)
                .params("minFat", 20)
                .params("maxFat", 100)
                .expect()
                .statusCode(200)
                .when()
                .get(getBaseUrl() + "recipes/complexSearch")
                .body()
                .jsonPath();
        assertThat(response.get("results[0].nutrition.nutrients[0].name"), equalTo("Fat"));
        assertThat(response.get("results[0].nutrition.nutrients[0].amount"), greaterThanOrEqualTo(20F));
        assertThat(response.get("results[0].nutrition.nutrients[0].amount"), lessThanOrEqualTo(100F));
    }

    @Test
    public void classifyCuisineItalianTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "pasta")
                .formParam("ingredientList", "flour\\n water\\n tomatoes")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .assertThat()
                .body("cuisine", equalTo("Mediterranean"))
                .body("cuisines[0]", equalTo("Mediterranean"))
                .body("cuisines[1]", equalTo("European"))
                .body("cuisines[2]", equalTo("Italian"));
    }

    @Test
    public void classifyCuisineAmericanTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "burger")
                .formParam("ingredientList", "pork\\n salad\\n bread")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .assertThat()
                .body("cuisine", equalTo("American"))
                .body("cuisines[0]", equalTo("American"))
                .body("confidence", equalTo(0.95F));
    }

    @Test
    public void classifyCuisineSpanishTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tapas")
                .formParam("ingredientList", "water\\n tomatoes")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .assertThat()
                .body("cuisine", equalTo("European"))
                .body("cuisines[0]", equalTo("European"))
                .body("cuisines[1]", equalTo("Spanish"))
                .body("confidence", equalTo(0.85F));
    }

    @Test
    public void classifyCuisineJapaneseTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Pork Belly or Chicken Yakitori")
                .formParam("ingredientList", "Pork\\n Chicken")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .assertThat()
                .body("cuisine", equalTo("Japanese"))
                .body("cuisines[0]", equalTo("Japanese"));
    }

    @Test
    public void classifyCuisineKoreanTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Kimchi Jjigae")
                .formParam("ingredientList", "guk")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .assertThat()
                .body("cuisine", equalTo("Korean"))
                .body("cuisines[0]", equalTo("Korean"));
    }

    @Test
    public void classifyCuisineChineseTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Peking Duck")
                .formParam("ingredientList", "Duck")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .assertThat()
                .body("cuisine", equalTo("Chinese"))
                .body("cuisines[0]", equalTo("Chinese"));
    }

    @ParameterizedTest
    @CsvSource (value = {"soup,meat,Mediterranean","burger,pork,American", "\"Pork roast with green beans\",\"3\\n oz\\n pork\\n shoulder\",Mediterranean"})
    public void classifyCuisineTest(String title, String ingredientList, String cuisine) {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", title)
                .formParam("ingredientList", ingredientList)
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .body("", Matchers.notNullValue())
                .body("cuisine", Matchers.equalTo(cuisine));
    }
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", delimiter = ',', numLinesToSkip = 1)
    public void classifyCuisineDataFromFileTest(String title, String ingredientList, String cuisine) {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", title)
                .formParam("ingredientList", ingredientList)
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .statusCode(200)
                .body("", Matchers.notNullValue())
                .body("cuisine", Matchers.equalTo(cuisine));
    }

}