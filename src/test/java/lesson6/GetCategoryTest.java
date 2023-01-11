package lesson6;

import lesson5.api.CategoryService;
import lesson5.dto.GetCategoryResponse;
import lesson5.utils.RetrofitUtils;
import lesson6.db.dao.CategoriesMapper;
import lesson6.db.model.Categories;
import lesson6.db.model.CategoriesExample;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetCategoryTest {

    static CategoryService categoryService;
    static SqlSession session = null;
    static String resource = "mybatis-config.xml";
    static SqlSessionFactory sqlSessionFactory;
    static CategoriesMapper categoriesMapper;
    static CategoriesExample example;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        session = sqlSessionFactory.openSession();
        categoriesMapper = session.getMapper(CategoriesMapper.class);
        example = new CategoriesExample();
    }

    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(1).execute();
        assert response.body() != null;
        assertThat(response.code(), equalTo(200));
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.body().getId(), equalTo(1));
        assertThat(response.body().getTitle(), equalTo("Food"));
        response.body().getProducts().forEach(product ->
                assertThat(product.getCategoryTitle(), equalTo("Food")));

        example.createCriteria().andIdGreaterThanOrEqualTo(1)
                .andTitleEqualTo("Food");
        List<Categories> list = categoriesMapper.selectByExample(example);

        assertThat(list.get(0).getId(), equalTo(1));
        assertThat(list.get(0).getTitle(), is("Food"));
    }
    @SneakyThrows
    @AfterAll
    static void afterAll() {
        session.commit();
        session.close();
    }


}
