package lesson4;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpoonacularTest extends AbstractTest {

    @Test
    public void searchRecipesWithoutParamsTest() {
        SearchRecipesComplexSearchResponse response = given()
                .spec(requestSpecification)
                .when()
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(SearchRecipesComplexSearchResponse.class);
        assertThat(response.getNumber(), is(10));
        assertThat(response.getOffset(), is(0));
    }

    @Test
    public void searchRecipesItalianVegetarianMainCurseTest() {
        SearchRecipesComplexSearchResponse response = given()
                .spec(getRequestSpecification())
                .params("cuisine", "italian")
                .params("diet", "vegetarian")
                .params("type", "main course")
                .params("number", 10)
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .spec(getResponseSpecification())
                .extract()
                .body()
                .as(SearchRecipesComplexSearchResponse.class);
        assertThat(response.getNumber(), is(10));
        assertThat(response.getOffset(), is(0));
    }

    @Test
    public void searchRecipesNumber_100Test() {
        SearchRecipesComplexSearchResponse response = given()
                .spec(requestSpecification)
                .params("cuisine", "italian")
                .params("number", 100)
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(SearchRecipesComplexSearchResponse.class);
        assertThat(response.getNumber(), is(100));
    }

    @Test
    public void searchRecipesCalories_500_800Test() {
        SearchRecipesComplexSearchResponse response = given()
                .spec(requestSpecification)
                .params("cuisine", "italian")
                .params("number", 10)
                .params("minCalories", 500)
                .params("maxCalories", 800)
                .when()
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(SearchRecipesComplexSearchResponse.class);
        assertThat(response.getResults().get(0).getNutrition().getNutrients().get(0).name, equalTo("Calories"));
        assertThat(response.getResults().get(0).getNutrition().getNutrients().get(0).amount, greaterThanOrEqualTo(500.0));
        assertThat(response.getResults().get(0).getNutrition().getNutrients().get(0).amount, lessThanOrEqualTo(800.0));
    }

    @Test
    public void searchRecipesFat_20_100Test() {
        SearchRecipesComplexSearchResponse response = given()
                .spec(requestSpecification)
                .params("cuisine", "italian")
                .params("number", 100)
                .params("minFat", 20)
                .params("maxFat", 100)
                .when()
                .get(getBaseUrl() + "recipes/complexSearch")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(SearchRecipesComplexSearchResponse.class);
        assertThat(response.getResults().get(0).getNutrition().getNutrients().get(0).name, equalTo("Fat"));
        assertThat(response.getResults().get(0).getNutrition().getNutrients().get(0).amount, greaterThanOrEqualTo(20.0));
        assertThat(response.getResults().get(0).getNutrition().getNutrients().get(0).amount, lessThanOrEqualTo(100.0));

    }

    @Test
    public void classifyCuisineItalianTest() {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "pasta")
                .formParam("ingredientList", "flour\\n water\\n tomatoes")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response.getCuisine(), equalTo("Mediterranean"));
        assertThat(response.getCuisines().get(0), equalTo("Mediterranean"));
        assertThat(response.getCuisines().get(1), equalTo("European"));
        assertThat(response.getCuisines().get(2), equalTo("Italian"));
    }

    @Test
    public void classifyCuisineAmericanTest() {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "burger")
                .formParam("ingredientList", "pork\\n salad\\n bread")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response.getCuisine(), equalTo("American"));
        assertThat(response.getCuisines().get(0), equalTo("American"));
        assertThat(response.getConfidence(), equalTo(0.95));
    }

    @Test
    public void classifyCuisineSpanishTest() {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tapas")
                .formParam("ingredientList", "water\\n tomatoes")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response.getCuisine(), is(equalTo("European")));
        assertThat(response.getCuisines().get(0), is(equalTo("European")));
        assertThat(response.getCuisines().get(1), is(equalTo("Spanish")));
        assertThat(response.getConfidence(), is(0.85));
    }

    @Test
    public void classifyCuisineJapaneseTest() {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Pork Belly or Chicken Yakitori")
                .formParam("ingredientList", "Pork\\n Chicken")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response.getCuisine(), is("Japanese"));
        assertThat(response.getCuisines().get(0), equalTo("Japanese"));
    }

    @Test
    public void classifyCuisineKoreanTest() {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Kimchi Jjigae")
                .formParam("ingredientList", "guk")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response.getCuisine(), is("Korean"));
        assertThat(response.getCuisines().get(0), equalTo("Korean"));
    }

    @Test
    public void classifyCuisineChineseTest() {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Peking Duck")
                .formParam("ingredientList", "Duck")
                .formParam("language", "en")
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response.getCuisine(), is(equalTo("Chinese")));
        assertThat(response.getCuisines().get(0), equalTo("Chinese"));
    }

    @ParameterizedTest
    @CsvSource(value = {"soup,meat,Mediterranean", "burger,pork,American", "\"Pork roast with green beans\",\"3\\n oz\\n pork\\n shoulder\",Mediterranean"})
    public void classifyCuisineTest(String title, String ingredientList, String cuisine) {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", title)
                .formParam("ingredientList", ingredientList)
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response, Matchers.notNullValue());
        assertThat(response.getCuisine(), Matchers.equalTo(cuisine));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", delimiter = ',', numLinesToSkip = 1)
    public void classifyCuisineDataFromFileTest(String title, String ingredientList, String cuisine) {
        ClassifyCuisineResponse response = given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", title)
                .formParam("ingredientList", ingredientList)
                .when()
                .post(getBaseUrl() + "recipes/cuisine")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ClassifyCuisineResponse.class);
        assertThat(response, Matchers.notNullValue());
        assertThat(response.getCuisine(), Matchers.equalTo(cuisine));
    }

}
