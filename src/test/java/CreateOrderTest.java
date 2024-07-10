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

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateOrderTest {
    private UserApi userApi;
    private OrderApi orderApi;
    private String acessToken;

    private static final boolean keySuccessExpected = true;
    private static final boolean keySuccessFalseExpected = false;

    private static final String messageNonIngredients = "Ingredient ids must be provided";

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
    @DisplayName("Проверка успешного создания заказа без авторизации. 200")
    public void createOrderWithoutAuthSuccess() {
        userApi = new UserApi();
        orderApi = new OrderApi();
        List<String> ingredientsForOrder = new ArrayList<>();
        ingredientsForOrder.add("61c0c5a71d1f82001bdaaa6d");
        ingredientsForOrder.add("61c0c5a71d1f82001bdaaa79");

        Order order = new Order();
        order.setIngredients(ingredientsForOrder);

        Response response = orderApi.postCreateOrder(order);
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        boolean successActual = response.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessExpected, successActual);

    }

    @Test
    @DisplayName("Проверка успешного создания заказа с авторизацией. 200")
    public void createOrderWithAuthSuccess() {
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

        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        Response responseOrder = orderApi.postCreateOrderwithAuth(acessToken, order);
        assertEquals("Неверный статус код", SC_OK, responseOrder.statusCode());
        boolean successActual = responseOrder.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessExpected, successActual);
        String ownerNameActual = responseOrder.path("order.owner.name");
        assertEquals("Некорректый ответ в Body owner.name", user.getName(), ownerNameActual);
        String ownerEmailActual = responseOrder.path("order.owner.email");
        assertEquals("Некорректый ответ в Body owner.email", user.getEmail(), ownerEmailActual);
    }

    @Test
    @DisplayName("Проверка создания заказа без ингридиентов. 400")
    public void createOrderWithoutIngredients() {
        userApi = new UserApi();
        orderApi = new OrderApi();
        List<String> ingredientsForOrder = new ArrayList<>();

        Order order = new Order();
        order.setIngredients(ingredientsForOrder);

        Response response = orderApi.postCreateOrder(order);
        assertEquals("Неверный статус код", SC_BAD_REQUEST, response.statusCode());
        boolean successActual = response.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = response.path("message");
        assertEquals("Некорректый ответ в Body success", messageNonIngredients, messageActual);
    }

    @Test
    @DisplayName("Проверка создания заказа с некорректным хэшем ингридиентов. 500")
    public void createOrderWithIncorrectHash() {
        userApi = new UserApi();
        orderApi = new OrderApi();
        List<String> ingredientsForOrder = new ArrayList<>();
        ingredientsForOrder.add("61c0c5a71");

        Order order = new Order();
        order.setIngredients(ingredientsForOrder);

        Response response = orderApi.postCreateOrder(order);
        // Проверяется только код ответа, так как тела в формате json нет
        assertEquals("Неверный статус код", SC_INTERNAL_SERVER_ERROR, response.statusCode());
    }
}
