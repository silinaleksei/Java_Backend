package lesson5;

import lesson5.api.CategoryService;
import lesson5.dto.GetCategoryResponse;
import lesson5.utils.RetrofitUtils;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetCategoryTest {

    static CategoryService categoryService;

    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(1).execute();
        assert response.body() != null;
        assertThat(response.code(), equalTo(200));
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(1));
        assertThat(response.body().getTitle(), equalTo("Food"));
        response.body().getProducts().forEach(product ->
                assertThat(product.getCategoryTitle(), equalTo("Food")));
    }

    @SneakyThrows
    @Test
    void getCategoryByIdZeroTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(0).execute();
        assert response.body() == null;
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }

    @SneakyThrows
    @Test
    void getCategoryByNotFoundTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(10).execute();
        assert response.body() == null;
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }
}
