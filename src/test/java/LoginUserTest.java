import com.github.javafaker.Faker;
import data.CreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserApi;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    private UserApi userApi;
    private String acessToken;

    private static final boolean keySuccessExpected = true;
    private static final boolean keySuccessFalseExpected = false;

    private static final String messageUnauthorized = "email or password are incorrect";
    @Before
    public void setUp() {
        RestAssured.baseURI= UrlConstants.BASE_URI;
    }
    @After
    public void teardown() {
        //Удаление пользователя
        userApi.deleteUser(acessToken);
    }

    @Test
    @DisplayName("Проверка успешной авторизации пользователя. 200")
    public void loginUserSuccess() {
        userApi = new UserApi();
        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withEmail(faker.internet().safeEmailAddress())
                .withPassword(faker.internet().password(3, 10))
                .withName(faker.name().firstName());

        //Регистрация пользователя для авторизации
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Авторизация пользователя
        Response responseLogin = userApi.loginUser(user);
        assertEquals("Неверный статус код", SC_OK, responseLogin.statusCode());
        boolean successActual = responseLogin.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessExpected, successActual);
    }

    @Test
    @DisplayName("Проверка авторизации незарегистрированного пользователя. 401")
    public void loginUserWithoutRegistration() {
        userApi = new UserApi();
        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withEmail(faker.internet().safeEmailAddress())
                .withPassword(faker.internet().password(3, 10))
                .withName(faker.name().firstName());

        //Авторизация незарегистрированного пользователя
        Response responseLogin = userApi.loginUser(user);
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseLogin.statusCode());
        boolean successActual = responseLogin.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = responseLogin.path("message");
        assertEquals("Некорректый ответ в Body message", messageUnauthorized, messageActual);
    }

    @Test
    @DisplayName("Проверка авторизации пользователя без email. 401")
    public void loginUserWithoutLogin() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userEmail = faker.internet().safeEmailAddress();
        String userPassword = faker.internet().password(3, 10);
        CreateUser user = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userPassword)
                .withName(faker.name().firstName());

        CreateUser userWithoutEmail = new CreateUser()
                .withPassword(userPassword);

        //Регистрация пользователя для авторизации
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Авторизация пользователя без email
        Response responseLogin = userApi.loginUser(userWithoutEmail);
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseLogin.statusCode());
        boolean successActual = responseLogin.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = responseLogin.path("message");
        assertEquals("Некорректый ответ в Body message", messageUnauthorized, messageActual);
    }

    @Test
    @DisplayName("Проверка авторизации пользователя без password. 401")
    public void loginUserWithoutPassword() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userEmail = faker.internet().safeEmailAddress();
        String userPassword = faker.internet().password(3, 10);
        CreateUser user = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userPassword)
                .withName(faker.name().firstName());

        CreateUser userWithoutPassword = new CreateUser()
                .withEmail(userEmail);

        //Регистрация пользователя для авторизации
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Авторизация пользователя без email
        Response responseLogin = userApi.loginUser(userWithoutPassword);
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseLogin.statusCode());
        boolean successActual = responseLogin.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = responseLogin.path("message");
        assertEquals("Некорректый ответ в Body message", messageUnauthorized, messageActual);
    }
}
