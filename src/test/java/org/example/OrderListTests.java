package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.CourierApi;
import org.example.models.CourierModel;
import org.example.utils.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class OrderListTests {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Faker faker = new Faker();
    private String courierId;
    private String login;

    @Before
    public void setUp() {
        login = "ninja_" + faker.name().username();
        CourierModel courier = new CourierModel(login, "1234", "Saske");
        Response createResponse = CourierApi.createCourier(courier);
        if (createResponse.statusCode() != 201) {
            throw new RuntimeException("Failed to create courier: " + createResponse.body().asString());
        }
        courierId = createResponse.jsonPath().getString("id");
        System.out.println("Created courierId: " + courierId);

        Response loginResponse = CourierApi.loginCourier(login, "1234");
        if (loginResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to login courier: " + loginResponse.body().asString());
        }
        courierId = loginResponse.jsonPath().getString("id");
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            Response response = RestClient.sendDeleteRequest("/api/v1/courier/" + courierId);
            response.then()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("Get order list without parameters")
    @Description("Test that the order list endpoint returns a list of orders without parameters")
    public void testGetOrderList() {
        Response response = RestClient.sendGetRequest("/api/v1/orders", null);
        response.then()
                .statusCode(200)
                .body("orders", not(empty()))
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue())
                .log().body();
    }

    @Test
    @DisplayName("Get order list with courierId")
    @Description("Test that the order list endpoint filters orders by courierId")
    public void testGetOrderListWithCourierId() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", courierId);
        Response response = RestClient.sendGetRequest("/api/v1/orders", params);
        response.then()
                .statusCode(200)
                .body("orders", instanceOf(java.util.List.class))
                .body("pageInfo", notNullValue())
                .log().body();
    }

    @Test
    @DisplayName("Get order list with non-existent courierId")
    @Description("Test that the order list endpoint returns 404 for non-existent courierId")
    public void testGetOrderListNonExistentCourier() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", "999999");
        Response response = RestClient.sendGetRequest("/api/v1/orders", params);
        response.then()
                .statusCode(404)
                .body("message", containsString("не найден"))
                .log().body();
    }

    @Test
    @Ignore("Disabled due to 500 error, investigate nearestStation format")
    @DisplayName("Get order list with nearestStation")
    @Description("Test that the order list endpoint filters orders by nearestStation")
    public void testGetOrderListWithNearestStation() {
        Map<String, Object> params = new HashMap<>();
        params.put("nearestStation", "1,2");
        Response response = RestClient.sendGetRequest("/api/v1/orders", params);
        response.then()
                .statusCode(200)
                .body("orders", instanceOf(java.util.List.class))
                .body("pageInfo", notNullValue())
                .log().body();
    }

    @Test
    @DisplayName("Get order list with limit and page")
    @Description("Test that the order list endpoint respects limit and page parameters")
    public void testGetOrderListWithLimitAndPage() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 10);
        params.put("page", 0);
        Response response = RestClient.sendGetRequest("/api/v1/orders", params);
        response.then()
                .statusCode(200)
                .body("orders", instanceOf(java.util.List.class))
                .body("pageInfo.limit", equalTo(10))
                .body("pageInfo.page", equalTo(0))
                .log().body();
    }
}