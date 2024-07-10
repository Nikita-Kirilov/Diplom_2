import com.github.javafaker.Faker;
import data.CreateUser;
import data.Order;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderApi;
import steps.UserApi;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class GetUserOrdersTest {
    private UserApi userApi;
    private OrderApi orderApi;
    private String acessToken;

    private static final boolean keySuccessExpected = true;
    private static final boolean keySuccessFalseExpected = false;

    private static final String messageNonAuth = "You should be authorised";

    @Before
    public void setUp() {
        RestAssured.baseURI = UrlConstants.BASE_URI;
    }

    @After
    public void teardown() {
        //Удаление пользователя
        userApi.deleteUser(acessToken);
    }

    @Test
    @DisplayName("Проверка успешного получения данных о заказе.Авторизованный клиент. 200")
    public void getOrderWithAuthSuccess() {
        userApi = new UserApi();
        orderApi = new OrderApi();

        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withEmail(faker.internet().safeEmailAddress())
                .withPassword(faker.internet().password(3, 10))
                .withName(faker.name().firstName());

        List<String> ingredientsForOrder = new ArrayList<>();
        ingredientsForOrder.add("61c0c5a71d1f82001bdaaa6d");
        ingredientsForOrder.add("61c0c5a71d1f82001bdaaa79");

        Order order = new Order();
        order.setIngredients(ingredientsForOrder);
        //Создание клиента
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Создание заказа
        orderApi.postCreateOrderwithAuth(acessToken, order);

        //Получение заказа
        Response responseGetOrder = orderApi.getOrderwithAuth(acessToken);
        assertEquals("Неверный статус код", SC_OK, responseGetOrder.statusCode());
        boolean successActual = responseGetOrder.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessExpected, successActual);
        int totalActual = responseGetOrder.path("total");
        int totalTodayActual = responseGetOrder.path("totalToday");
        int totalAndTotalTodayExpected=1;
        assertEquals("Некорректый ответ в Body total", totalAndTotalTodayExpected, totalActual);
        assertEquals("Некорректый ответ в Body totalToday", totalAndTotalTodayExpected, totalTodayActual);
    }

    @Test
    @DisplayName("Проверка успешного получения данных о заказе.Неавторизованный клиент. 401")
    public void getOrderWithoutAuthSuccess() {
        userApi = new UserApi();
        orderApi = new OrderApi();

        //Получение заказа
        Response responseGetOrder = orderApi.getOrderwithoutAuth();
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseGetOrder.statusCode());
        boolean successActual = responseGetOrder.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = responseGetOrder.path("message");
        assertEquals("Некорректый ответ в Body message", messageNonAuth, messageActual);
    }
}
