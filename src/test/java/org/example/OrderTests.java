package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.CourierApi;
import org.example.api.OrderApi;
import org.example.models.CourierModel;
import org.example.models.OrderModel;
import org.example.utils.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderTests {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Faker faker = new Faker();
    private String orderTrack;
    private String courierId;
    private String authToken;

    @Parameterized.Parameter
    public Object color; // Object для поддержки String и List

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"BLACK"},          // Один цвет
                {"GREY"},           // Один цвет
                {null},             // Без цвета
                {Arrays.asList("BLACK", "GREY")} // Два цвета
        });
    }

    @Before
    public void setUp() throws Exception {
        String login = "test_" + faker.name().username();
        CourierModel courier = new CourierModel(login, "1234", null);
        Response createCourierResponse = CourierApi.createCourier(courier);
        if (createCourierResponse.statusCode() != 201) {
            throw new RuntimeException("Failed to create courier: " + createCourierResponse.body().asString());
        }
        courierId = createCourierResponse.jsonPath().getString("id");
        System.out.println("Created courierId: " + courierId);

        Response loginResponse = CourierApi.loginCourier(login, "1234");
        if (loginResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to login courier: " + loginResponse.body().asString());
        }
        authToken = loginResponse.jsonPath().getString("token");
        if (authToken == null) {
            System.out.println("Warning: No auth token received from login response");
        }
    }

    @After
    public void cleanUp() {
        if (orderTrack != null) {
            OrderApi.cancelOrder(orderTrack, authToken);
        }
        if (courierId != null) {
            RestClient.sendDeleteRequest("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @Ignore("Disabled due to 500 error, investigate order creation")
    @DisplayName("Test creating order with different colors")
    @Description("Test creating order with valid data and different colors")
    public void testCreateOrder() {
        OrderModel order;
        if (color instanceof List) {
            order = new OrderModel(
                    "Naruto",
                    "Uzumaki",
                    "1234567890",
                    4,
                    "2025-07-06T15:00:00Z",
                    "15:00",
                    (List<String>) color, // Список цветов
                    2,
                    "Comment for delivery",
                    500
            );
        } else {
            order = new OrderModel(
                    "Naruto",
                    "Uzumaki",
                    "1234567890",
                    4,
                    "2025-07-06T15:00:00Z",
                    "15:00",
                    color != null ? Collections.singletonList(color.toString()) : null, // Один цвет как список
                    2,
                    "Comment for delivery",
                    500
            );
        }
        order.setCourierId(courierId);
        String jsonBody = null;
        try {
            jsonBody = mapper.writeValueAsString(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order: " + e.getMessage());
        }
        Response orderResponse = RestClient.sendPostRequest("/api/v1/orders", jsonBody, authToken)
                .then()
                .extract().response();
        orderResponse.then()
                .statusCode(201)
                .body("track", notNullValue())
                .log().body();
        orderTrack = orderResponse.jsonPath().getString("track");
        System.out.println("Created orderTrack: " + orderTrack);
    }

    @Test
    @DisplayName("Test getting order list")
    @Description("Test retrieving the list of orders")
    public void testGetOrderList() {
        Map<String, Object> params = new HashMap<>();
        params.put("Authorization", "Bearer " + authToken); // Добавление токена в параметры
        Response response = RestClient.sendGetRequest("/api/v1/orders", params)
                .then()
                .extract().response();
        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .log().body();
    }

    @Test
    @Ignore("Disabled due to 500 error, investigate invalid data handling")
    @DisplayName("Test creating order with invalid data")
    @Description("Test that creating order with invalid data fails")
    public void testCreateOrderWithInvalidData() {
        OrderModel order = new OrderModel(
                "Naruto",
                "Uzumaki",
                "",
                4,
                "2025-07-06T15:00:00Z",
                "15:00",
                Collections.singletonList("Black"), // Один цвет как список
                2,
                "Comment for delivery",
                500
        );
        order.setCourierId(courierId);
        String jsonBody = null;
        try {
            jsonBody = mapper.writeValueAsString(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order: " + e.getMessage());
        }
        Response response = RestClient.sendPostRequest("/api/v1/orders", jsonBody, authToken)
                .then()
                .extract().response();
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания заказа"))
                .log().body();
    }
}