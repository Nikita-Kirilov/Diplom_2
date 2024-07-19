package steps;

import config.AppConfig;
import data.CreateUser;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;

import static io.restassured.RestAssured.given;

public class UserApi {

    private AppConfig appConfig = ConfigFactory.create(AppConfig.class);
    private static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_USER_ENDPOINT = "/api/auth/login";
    private static final String DELETE_UPDATE_USER_ENDPOINT = "/api/auth/user";

    @Step("Send POST request to /api/auth/register")
    @Description("Запрос на создание пользователя")
    public Response postCreateUser(CreateUser user) {
        return given().baseUri(appConfig.baseUrl())
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_ENDPOINT);
    }

    @Step("Send DELETE request to /api/auth/user")
    @Description("Запрос на удаление данных пользователя")
    public void deleteUser(String token) {
        if(token != null) {
            given().baseUri(appConfig.baseUrl())
                    .auth().oauth2(token)
                    .when()
                    .delete(DELETE_UPDATE_USER_ENDPOINT);
        }
    }

    @Step("Send PATCH request to /api/auth/user")
    @Description("Запрос на обновление данных пользователя")
    public Response patchUser(String token, CreateUser user) {
        return given().baseUri(appConfig.baseUrl())
                    .header("Content-type", "application/json")
                    .auth().oauth2(token)
                    .and()
                    .body(user)
                    .when()
                    .patch(DELETE_UPDATE_USER_ENDPOINT);
    }

    @Step("Send no auth PATCH request to /api/auth/user")
    @Description("Запрос на обновление данных пользователя")
    public Response patchUserNoAuth(CreateUser user) {
        return given().baseUri(appConfig.baseUrl())
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(DELETE_UPDATE_USER_ENDPOINT);
    }

    @Step("Send POST request to /auth/login")
    @Description("Запрос на авторизацию пользователя")
    public Response loginUser(CreateUser user) {
        return given().baseUri(appConfig.baseUrl())
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(LOGIN_USER_ENDPOINT);
    }
}
