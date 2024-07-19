package steps;

import config.AppConfig;
import data.CreateUser;
import data.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private AppConfig appConfig = ConfigFactory.create(AppConfig.class);
    private static final String GET_CREATE_ORDER_ENDPOINT = "/api/orders";

    private static final String GET_INGREDIENTS_ENDPOINT = "/api/ingredients";

    @Step("Send POST request to /api/orders")
    @Description("Запрос на создание заказа")
    public Response postCreateOrder(Order order) {
        return given().baseUri(appConfig.baseUrl())
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(GET_CREATE_ORDER_ENDPOINT);
    }

    @Step("Send POST with auth request to /api/orders")
    @Description("Запрос на создание заказа с авторизацией")
    public Response postCreateOrderwithAuth(String token,Order order) {
        return given().baseUri(appConfig.baseUrl())
                .auth().oauth2(token)
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(GET_CREATE_ORDER_ENDPOINT);
    }

    @Step("Send GET with auth request to /api/orders")
    @Description("Запрос на получение заказов клиента с авторизацией")
    public Response getOrderwithAuth(String token) {
        return given().baseUri(appConfig.baseUrl())
                .auth().oauth2(token)
                .when()
                .get(GET_CREATE_ORDER_ENDPOINT);
    }

    @Step("Send GET with auth request to /api/orders")
    @Description("Запрос на получение заказов клиента без авторизации")
    public Response getOrderwithoutAuth() {
        return given().baseUri(appConfig.baseUrl())
                .when()
                .get(GET_CREATE_ORDER_ENDPOINT);
    }

    @Step("Send GET request to /api/ingredients")
    @Description("Запрос на получение ингредиентов")
    public Response getIngredients() {
        return given().baseUri(appConfig.baseUrl())
                .when()
                .get(GET_INGREDIENTS_ENDPOINT);
    }
}
