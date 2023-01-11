package lesson6;

import com.github.javafaker.Faker;
import lesson5.api.ProductService;
import lesson5.dto.Product;
import lesson5.utils.RetrofitUtils;
import lesson6.db.dao.CategoriesMapper;
import lesson6.db.dao.ProductsMapper;
import lesson6.db.model.Categories;
import lesson6.db.model.Products;
import lesson6.db.model.ProductsExample;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;


public class ProductServiceTest {

    static ProductService productService;
    static SqlSession sqlSession = null;
    static ProductsMapper productsMapper = null;
    static CategoriesMapper categoriesMapper = null;
    Product product = null;
    Faker faker = new Faker();
    int id;
    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
        SqlSessionFactory sqlSessionFactory;
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
        productsMapper = sqlSession.getMapper(ProductsMapper.class);
        categoriesMapper = sqlSession.getMapper(CategoriesMapper.class);
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

        Products products = productsMapper.selectByPrimaryKey((long) id);
        Categories categories = categoriesMapper.selectByPrimaryKey(Math.toIntExact(products.getCategory_id()));
        assertThat(products.getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(categories.getTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(products.getPrice(), equalTo(product.getPrice()));
        sqlSession.commit();

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

        products = productsMapper.selectByPrimaryKey((long) id);
        categories = categoriesMapper.selectByPrimaryKey(Math.toIntExact(products.getCategory_id()));
        assertThat(products.getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(categories.getTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(products.getPrice(), equalTo(product.getPrice()));
        sqlSession.commit();

    }

    @SneakyThrows
    @AfterEach
    void deleteProduct() {
        Response<ResponseBody> response = productService.deleteProduct(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));

        ProductsExample productExample = new ProductsExample();
        productExample.createCriteria().andIdEqualTo((long) id);
        List<Products> productsList = productsMapper.selectByExample(productExample);
        sqlSession.commit();
        assertThat(productsList.size(), equalTo(0));
    }
    @AfterAll
    static void tearDown(){
        sqlSession.close();
    }

}
