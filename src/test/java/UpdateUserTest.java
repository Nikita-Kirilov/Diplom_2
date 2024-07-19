import com.github.javafaker.Faker;
import data.CreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserApi;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class UpdateUserTest {
    private UserApi userApi;
    private String acessToken;

    private static final boolean KEY_SUCCESS_EXPECTED = true;
    private static final boolean KEY_SUCCESS_FALSE_EXPECTED = false;

    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";

    @After
    public void teardown() {
        //Удаление пользователя
        userApi.deleteUser(acessToken);
    }

    @Test
    @DisplayName("Проверка успешного изменения email. 200")
    public void updateUserEmailSuccess() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userPassword = faker.internet().password(3, 10);
        String userOldEmail = faker.internet().safeEmailAddress();
        String userName = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(userOldEmail)
                .withPassword(userPassword)
                .withName(userName);

        String userNewEmail = faker.internet().safeEmailAddress();
        CreateUser userUpdateEmail = new CreateUser()
                .withEmail(userNewEmail)
                .withPassword(userPassword)
                .withName(userName);

        //Регистрация пользователя для последующего обновления email
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Обновление email пользователя
        Response responseUpdate = userApi.patchUser(acessToken,userUpdateEmail);
        assertEquals("Неверный статус код", SC_OK, responseUpdate.statusCode());
        boolean successActual = responseUpdate.path("success");
        assertEquals("Некорректый ответ в Body success", KEY_SUCCESS_EXPECTED, successActual);
        String newEmailActual = responseUpdate.path("user.email");
        assertEquals("Не обновился email ответа Body", userNewEmail, newEmailActual);
    }

    @Test
    @DisplayName("Проверка успешного изменения name. 200")
    public void updateUserNameSuccess() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userPassword = faker.internet().password(3, 10);
        String userEmail = faker.internet().safeEmailAddress();
        String userOldName = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userPassword)
                .withName(userOldName);

        String userNewName = faker.name().firstName();
        CreateUser userUpdateName = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userPassword)
                .withName(userNewName);

        //Регистрация пользователя для последующего обновления name
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Обновление name пользователя
        Response responseUpdate = userApi.patchUser(acessToken,userUpdateName);
        assertEquals("Неверный статус код", SC_OK, responseUpdate.statusCode());
        boolean successActual = responseUpdate.path("success");
        assertEquals("Некорректый ответ в Body success", KEY_SUCCESS_EXPECTED, successActual);
        String newNameActual = responseUpdate.path("user.name");
        assertEquals("Не обновился name ответа Body", userNewName, newNameActual);
    }

    @Test
    @DisplayName("Проверка успешного изменения password. 200")
    public void updateUserPasswordSuccess() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userOldPassword = faker.internet().password(3, 10);
        String userEmail = faker.internet().safeEmailAddress();
        String userName = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userOldPassword)
                .withName(userName);

        String userNewPassword = faker.internet().password(3, 10);
        CreateUser userUpdatePassword = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userNewPassword)
                .withName(userName);

        //Регистрация пользователя для последующего обновления password
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Обновление password пользователя
        Response responseUpdate = userApi.patchUser(acessToken,userUpdatePassword);
        assertEquals("Неверный статус код", SC_OK, responseUpdate.statusCode());
        boolean successActual = responseUpdate.path("success");
        assertEquals("Некорректый ответ в Body success", KEY_SUCCESS_EXPECTED, successActual);
    }

    @Test
    @DisplayName("Проверка изменения name без авторизации. 401")
    public void updateUserNameNotAuth() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userPassword = faker.internet().password(3, 10);
        String userEmail = faker.internet().safeEmailAddress();
        String userOldName = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userPassword)
                .withName(userOldName);

        String userNewName = faker.name().firstName();
        CreateUser userUpdateName = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userPassword)
                .withName(userNewName);

        //Регистрация пользователя для последующего обновления name
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Обновление name пользователя
        Response responseUpdate = userApi.patchUserNoAuth(userUpdateName);
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseUpdate.statusCode());
        boolean successActual = responseUpdate.path("success");
        assertEquals("Некорректый ответ в Body success", KEY_SUCCESS_FALSE_EXPECTED, successActual);
        String messageActual = responseUpdate.path("message");
        assertEquals("Неверный текст message Body", MESSAGE_UNAUTHORIZED, messageActual);
    }

    @Test
    @DisplayName("Проверка изменения email без авторизации. 401")
    public void updateUserEmailNotAuth() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userPassword = faker.internet().password(3, 10);
        String userOldEmail = faker.internet().safeEmailAddress();
        String userName = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(userOldEmail)
                .withPassword(userPassword)
                .withName(userName);

        String userNewEmail = faker.internet().safeEmailAddress();
        CreateUser userUpdateEmail = new CreateUser()
                .withEmail(userNewEmail)
                .withPassword(userPassword)
                .withName(userName);

        //Регистрация пользователя для последующего обновления email
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Обновление email пользователя
        Response responseUpdate = userApi.patchUserNoAuth(userUpdateEmail);
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseUpdate.statusCode());
        boolean successActual = responseUpdate.path("success");
        assertEquals("Некорректый ответ в Body success", KEY_SUCCESS_FALSE_EXPECTED, successActual);
        String messageActual = responseUpdate.path("message");
        assertEquals("Неверный текст message Body", MESSAGE_UNAUTHORIZED, messageActual);
    }

    @Test
    @DisplayName("Проверка изменения password без авторизации. 401")
    public void updateUserPasswordNotAuth() {
        userApi = new UserApi();
        Faker faker = new Faker();
        String userOldPassword = faker.internet().password(3, 10);
        String userEmail = faker.internet().safeEmailAddress();
        String userName = faker.name().firstName();
        CreateUser user = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userOldPassword)
                .withName(userName);

        String userNewPassword = faker.internet().password(3, 10);
        CreateUser userUpdatePassword = new CreateUser()
                .withEmail(userEmail)
                .withPassword(userNewPassword)
                .withName(userName);

        //Регистрация пользователя для последующего обновления password
        Response response = userApi.postCreateUser(user);
        acessToken = response.path("accessToken");
        acessToken = acessToken.substring(7);

        //Обновление password пользователя
        Response responseUpdate = userApi.patchUserNoAuth(userUpdatePassword);
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, responseUpdate.statusCode());
        boolean successActual = responseUpdate.path("success");
        assertEquals("Некорректый ответ в Body success", KEY_SUCCESS_FALSE_EXPECTED, successActual);
        String messageActual = responseUpdate.path("message");
        assertEquals("Неверный текст message Body", MESSAGE_UNAUTHORIZED, messageActual);
    }
}
