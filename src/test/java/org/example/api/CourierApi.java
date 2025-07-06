package org.example.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.models.CourierModel;
import org.example.utils.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;

public class CourierApi {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Step("Create a new courier")
    public static Response createCourier(CourierModel courier) {
        try {
            String jsonBody = mapper.writeValueAsString(courier);
            return given()
                    .spec(RestClient.getBaseSpec())
                    .body(jsonBody)
                    .log().all() // Логирование для отладки
                    .when()
                    .post("/api/v1/courier");
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize courier model", e);
        }
    }

    @Step("Login courier with credentials")
    public static Response loginCourier(String login, String password) {
        String jsonBody = "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
        return given()
                .spec(RestClient.getBaseSpec())
                .body(jsonBody)
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