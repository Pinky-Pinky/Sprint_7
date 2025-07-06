package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.CourierApi;
import org.example.models.CourierModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierLoginTests {
    private static final ObjectMapper mapper = new ObjectMapper();
    private String courierId;
    private String login;

    @Before
    public void setUp() throws Exception {
        login = "ninja" + System.currentTimeMillis(); // Уникальный логин
        CourierModel courier = new CourierModel(login, "1234", null);
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all() // Логирование ответа
                .statusCode(201); // Ожидаем 201
        courierId = response.jsonPath().getString("id"); // Проверяем, есть ли id
        System.out.println("Created courierId: " + courierId);
        // Логин для получения ID, если нужно
        Response loginResponse = CourierApi.loginCourier(login, "1234");
        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue());
        courierId = loginResponse.jsonPath().getString("id"); // Обновляем ID после логина
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            CourierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Courier can login")
    @Description("Test that a courier can login with valid credentials")
    public void testCourierCanLogin() {
        Response response = CourierApi.loginCourier(login, "1234");
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Login with wrong password")
    @Description("Test that login fails with incorrect password")
    public void testLoginWithWrongPassword() {
        Response response = CourierApi.loginCourier(login, "wrong");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login with non-existent user")
    @Description("Test that login fails for non-existent user")
    public void testLoginNonExistentUser() {
        String nonExistentLogin = "nonexistent" + System.currentTimeMillis();
        Response response = CourierApi.loginCourier(nonExistentLogin, "1234");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Ignore("Пропуск из-за SocketTimeoutException на сервере")
    @DisplayName("Login missing field")
    @Description("Test that login fails when a required field is missing")
    public void testLoginMissingField() {
        Response response = CourierApi.loginCourier(login, "");
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}