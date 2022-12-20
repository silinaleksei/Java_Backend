package lesson3;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;


public class MealPlanChainTest extends AbstractTest {

    private static Integer Id;
    private static String username;
    private static String hash;
    private static String date;
    private static Long unixTime;

    @BeforeAll
    static void setUp() {
        date = LocalDate.now().toString();
        unixTime = Instant.now().getEpochSecond();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    @DisplayName("Create new user")
    public void connectUser() {
        JsonPath jsonPath = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "")
                .contentType("application/json")
                .body("""
                        {
                            "username": alekseisilin,
                            "firstName": Aleksei,
                            "lastName": Silin,
                            "email": silinaleksei@gmail.com
                        }""")
                .post(getBaseUrl() + "users/connect")
                .then()
                .statusCode(200)
                .extract()  // Извлечь
                .body()
                .jsonPath();
        username = jsonPath.getString("username");
        hash = jsonPath.getString("hash");
        System.out.println("username:" + username);
        System.out.println("hash:" + hash);
    }

    @Test
    void addToMealPlanAndGetPlanTest() {
        String id = given()
                .queryParam("apiKey", getApiKey())
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"date\": " + unixTime + ",\n" +
                        "    \"slot\": 1,\n" +
                        "    \"position\": 0,\n" +
                        "    \"type\": \"INGREDIENTS\",\n" +
                        "    \"value\": {\n" +
                        "        \"ingredients\": [\n" +
                        "            {\n" +
                        "                \"name\": \"1 banana\"\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}")
                .queryParam("hash", hash)
                .pathParam("username", username)
                .when()
                .post(getBaseUrl() + "mealplanner/{username}/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
        System.out.println("id: " + id);
        Id = Integer.parseInt(id);

        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", hash)
                .pathParam("username", username)
                .pathParam("date", date)
                .when()
                .get(getBaseUrl() + "mealplanner/{username}/day/{date}")
                //.prettyPeek()
                .then()
                .statusCode(200);

//        //given()
//                .queryParam("apiKey", getApiKey())
//                .queryParam("hash", hash)
//                .pathParam("username", username)
//                .when()
//                .delete(getBaseUrl() + "mealplanner/{username}/items/" + id)
//                .then()
//                .statusCode(200);
    }

    @AfterAll
    @DisplayName("Delete plan")
    static void tearDown() {
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", hash)
                .pathParam("username", username)
                //.log().all()
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/items/" + Id)
                .then()
                .statusCode(200);
    }
}