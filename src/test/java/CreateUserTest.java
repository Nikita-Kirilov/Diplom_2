import com.github.javafaker.Faker;
import data.CreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserApi;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

public class CreateUserTest {

    private UserApi userApi;
    private String acessToken;

    private static final boolean keySuccessExpected = true;
    private static final boolean keySuccessFalseExpected = false;

    private static final String messageAlreadyExists = "User already exists";
    private static final String messageFieldRequired = "Email, password and name are required fields";
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
    @DisplayName("Проверка успешного создания пользователя. 200")
    public void createUserSuccess() {
        userApi = new UserApi();
        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withEmail(faker.internet().safeEmailAddress())
                .withPassword(faker.internet().password(3, 10))
                .withName(faker.name().firstName());

        Response response = userApi.postCreateUser(user);
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        boolean successActual = response.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessExpected, successActual);

        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован. 403")
    public void createSameUser() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String emailUser = faker.internet().safeEmailAddress();
        String passwordUser = faker.internet().password(3, 10);
        String nameUser = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(emailUser)
                .withPassword(passwordUser)
                .withName(nameUser);

        //Содание первого пользователя
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Создание второго пользователя с одинаковыми данными
        Response responseSameUser = userApi.postCreateUser(user);
        assertEquals("Неверный статус код", SC_FORBIDDEN, responseSameUser.statusCode());
        boolean successActual = responseSameUser.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = responseSameUser.path("message");
        assertEquals("Некорректый ответ в Body message", messageAlreadyExists, messageActual);

    }

    @Test
    @DisplayName("Проверка создания пользователя без email. 403")
    public void createUserWithoutEmail() {
        userApi = new UserApi();
        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withPassword(faker.internet().password(3, 10))
                .withName(faker.name().firstName());

        Response response = userApi.postCreateUser(user);
        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        boolean successActual = response.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = response.path("message");
        assertEquals("Некорректый ответ в Body message", messageFieldRequired, messageActual);
    }

    @Test
    @DisplayName("Проверка создания пользователя без password. 403")
    public void createUserWithoutPassword() {
        userApi = new UserApi();
        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withEmail(faker.internet().safeEmailAddress())
                .withName(faker.name().firstName());

        Response response = userApi.postCreateUser(user);
        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        boolean successActual = response.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = response.path("message");
        assertEquals("Некорректый ответ в Body message", messageFieldRequired, messageActual);
    }

    @Test
    @DisplayName("Проверка создания пользователя без name. 403")
    public void createUserWithoutName() {
        userApi = new UserApi();
        Faker faker = new Faker();
        CreateUser user = new CreateUser()
                .withEmail(faker.internet().safeEmailAddress())
                .withPassword(faker.internet().password(3, 10));

        Response response = userApi.postCreateUser(user);
        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        boolean successActual = response.path("success");
        assertEquals("Некорректый ответ в Body success", keySuccessFalseExpected, successActual);
        String messageActual = response.path("message");
        assertEquals("Некорректый ответ в Body message", messageFieldRequired, messageActual);
    }

}
