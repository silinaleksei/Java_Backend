package lesson4;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

public class ShoppingListTest extends AbstractTest {
    private static Integer Id;
    private static String username;
    private static String hash;
    static ResponseSpecification responseSpecification;
    static RequestSpecification requestSpecification;

    @BeforeAll
    @DisplayName("Create new user")
    static public void connectUser() {
        Faker faker = new Faker();
        UsersConnectRequest usersConnectRequest = new UsersConnectRequest();
        usersConnectRequest.setEmail(faker.internet().emailAddress());
        usersConnectRequest.setUsername(faker.funnyName().name());
        usersConnectRequest.setFirstName(faker.name().firstName());
        usersConnectRequest.setLastName(faker.name().lastName());


        UsersConnectResponse response = given()
                .spec(getRequestSpecification())
                .body(usersConnectRequest)
                .when()
                .post(getBaseUrl() + "users/connect")
                .then()
                .spec(getResponseSpecification())
                .extract()
                .body()
                .as(UsersConnectResponse.class);
        hash = response.getHash();
        username = response.getUsername();
        System.out.println("username:" + username);
        System.out.println("hash:" + hash);
    }

    @BeforeEach
    void beforeTest() {
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();

        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", getApiKey())
                .addQueryParam("hash", hash)
                .addPathParam("username", username)
                .log(LogDetail.ALL)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void shoppingListTest() {
        RequestShoppingList request = new RequestShoppingList();
        request.setItem("1 package baking powder");
        request.setAisle("Baking");
        request.setParse(true);

        ResponseShoppingList response = given()
                .spec(requestSpecification)
                .body(request)
                .when()
                .post(getBaseUrl() + "mealplanner/{username}/shopping-list/items")
                .prettyPeek()
                .then()
                .spec(responseSpecification)
                .extract()
                .response()
                .body()
                .as(ResponseShoppingList.class);
        Id = response.getId();

        given().spec(requestSpecification)
                .when()
                .get(getBaseUrl() + "mealplanner/{username}/shopping-list")
                .prettyPeek()
                .then()
                .spec(responseSpecification);


//        //given().spec(requestSpecification)
//                .when()
//                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + response.getId())
//                .prettyPeek()
//                .then()
//                .spec(responseSpecification);
    }


    @AfterAll
    @DisplayName("Delete list")
    static void tearDown() {
        given().spec(requestSpecification)
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + Id)
                .prettyPeek()
                .then()
                .spec(responseSpecification);
    }
}

