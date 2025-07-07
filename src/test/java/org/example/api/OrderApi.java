package org.example.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.utils.RestClient;

import static io.restassured.RestAssured.given;

public class OrderApi {
    @Step("Cancel order with track")
    public static void cancelOrder(String track) {
        given()
                .spec(RestClient.getBaseSpec())
                .delete("/api/v1/orders/cancel/" + track);
    }
}