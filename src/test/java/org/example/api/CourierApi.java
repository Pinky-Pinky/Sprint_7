package org.example.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.models.CourierModel;
import org.example.utils.RestClient;

import static io.restassured.RestAssured.given;

public class CourierApi {
    // Модель для логина (оставляем как есть)
    public static class LoginRequest {
        private String login;
        private String password;

        public LoginRequest(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() { return login; }
        public String getPassword() { return password; }
    }

    @Step("Create a new courier")
    public static Response createCourier(CourierModel courier) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(courier) // Прямое использование объекта, Gson сериализует
                .log().all() // Логирование для отладки
                .when()
                .post("/api/v1/courier");
    }

    @Step("Login courier with credentials")
    public static Response loginCourier(String login, String password) {
        LoginRequest loginRequest = new LoginRequest(login, password);
        return given()
                .spec(RestClient.getBaseSpec())
                .body(loginRequest) // Прямое использование объекта, Gson сериализует
                .log().all() // Логирование для отладки
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Delete courier by ID")
    public static void deleteCourier(String courierId) {
        given()
                .spec(RestClient.getBaseSpec())
                .delete("/api/v1/courier/" + courierId);
    }
}