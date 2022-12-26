package lesson5;

import com.github.javafaker.Faker;
import lesson5.api.ProductService;
import lesson5.dto.Product;
import lesson5.utils.RetrofitUtils;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;


public class ProductServiceTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
    }


    @SneakyThrows
    @Test
    void productServiceTest() {
        Response<ResponseBody> responseBody = productService.getProducts()
                .execute();
        assertThat(responseBody.code(), equalTo(200));
        assertThat(responseBody.isSuccessful(), CoreMatchers.is(true));
        assertThat(responseBody.headers().get("content-type"), CoreMatchers.is("application/json"));

        Response<Product> response = productService.createProduct(product).execute();
        assert response.body() != null;
        id = response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));


        response = productService.getProductById(id).execute();
        assert response.body() != null;
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));

        product = new Product()
                .withId(id)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
        response = productService.modifyProduct(product).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assert response.body() != null;
        assertThat(response.body().getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));

        // Create Product in food category with id -> 400 (Undocumented)
        product = new Product()
                .withId(1)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
        response = productService.createProduct(product).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(400));
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> response = productService.deleteProduct(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
    }


}
