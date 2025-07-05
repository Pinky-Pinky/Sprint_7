package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderListTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private String courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        // Create a courier for testing
        String login = "ninja" + System.currentTimeMillis();
        String body = "{\"login\": \"" + login + "\", \"password\": \"1234\", \"firstName\": \"saske\"}";
        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body("{\"login\": \"" + login + "\", \"password\": \"1234\"}")
                .post("/api/v1/courier/login");
        courierId = loginResponse.jsonPath().getString("id");
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            given()
                    .header("Content-Type", "application/json")
                    .delete("/api/v1/courier/" + courierId);
        }
    }

    @Test
    @DisplayName("Get order list without parameters")
    @Description("Test that the order list endpoint returns a list of orders")
    public void testGetOrderList() {
        given()
                .header("Content-Type", "application/json")
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", not(empty()))
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue());
    }

    @Test
    @DisplayName("Get order list with courierId")
    @Description("Test that the order list endpoint filters by courierId")
    public void testGetOrderListWithCourierId() {
        given()
                .header("Content-Type", "application/json")
                .queryParam("courierId", courierId)
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", instanceOf(java.util.List.class))
                .body("pageInfo", notNullValue());
    }

    @Test
    @DisplayName("Get order list with non-existent courierId")
    @Description("Test that the order list endpoint returns 404 for non-existent courierId")
    public void testGetOrderListNonExistentCourier() {
        given()
                .header("Content-Type", "application/json")
                .queryParam("courierId", "999999")
                .get("/api/v1/orders")
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором 999999 не найден"));
    }

    @Test
    @DisplayName("Get order list with nearestStation")
    @Description("Test that the order list endpoint filters by nearestStation")
    public void testGetOrderListWithNearestStation() {
        given()
                .header("Content-Type", "application/json")
                .queryParam("nearestStation", "[\"1\", \"2\"]")
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", instanceOf(java.util.List.class))
                .body("pageInfo", notNullValue());
    }

    @Test
    @DisplayName("Get order list with limit and page")
    @Description("Test that the order list endpoint respects limit and page parameters")
    public void testGetOrderListWithLimitAndPage() {
        given()
                .header("Content-Type", "application/json")
                .queryParam("limit", 10)
                .queryParam("page", 0)
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", instanceOf(java.util.List.class))
                .body("pageInfo.limit", equalTo(10))
                .body("pageInfo.page", equalTo(0));
    }
}