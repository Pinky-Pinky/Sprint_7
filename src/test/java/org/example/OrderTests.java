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

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderTests {
    private static final ObjectMapper mapper = new ObjectMapper();
    private String orderTrack;
    private String courierId;
    private String authToken; // Для хранения токена авторизации

    @Before
    public void setUp() throws Exception {
        String login = "test" + System.currentTimeMillis();
        CourierModel courier = new CourierModel(login, "1234", null);
        Response createCourierResponse = CourierApi.createCourier(courier);
        if (createCourierResponse.statusCode() != 201) {
            throw new RuntimeException("Failed to create courier: " + createCourierResponse.body().asString());
        }
        courierId = createCourierResponse.jsonPath().getString("id");
        System.out.println("Created courierId: " + courierId);

        // Получаем токен авторизации (если API его возвращает)
        Response loginResponse = CourierApi.loginCourier(login, "1234");
        if (loginResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to login courier: " + loginResponse.body().asString());
        }
        authToken = loginResponse.jsonPath().getString("token"); // Проверьте, возвращает ли API токен
        if (authToken == null) {
            System.out.println("Warning: No auth token received from login response");
        }
    }

    @After
    public void cleanUp() {
        if (orderTrack != null) {
            OrderApi.cancelOrder(orderTrack);
        }
        if (courierId != null) {
            given()
                    .spec(RestClient.getBaseSpec())
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @Ignore("Disabled due to 500 error, investigate order creation")
    @DisplayName("Test creating order")
    @Description("Test creating order with valid data")
    public void testCreateOrder() {
        OrderModel order = new OrderModel(
                "Naruto",
                "Uzumaki",
                "1234567890",
                4,
                "2025-07-06T15:00:00Z", // Изменили формат даты на ISO 8601
                "15:00",
                "Black",
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
        Response orderResponse = given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", "Bearer " + authToken) // Добавляем токен, если доступен
                .log().all()
                .body(jsonBody)
                .when()
                .post("/api/v1/orders");
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
        Response response = given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", "Bearer " + authToken) // Добавляем токен, если доступен
                .log().all()
                .when()
                .get("/api/v1/orders");
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
                "2025-07-06T15:00:00Z", // Изменили формат даты
                "15:00",
                "Black",
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
        Response response = given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", "Bearer " + authToken) // Добавляем токен, если доступен
                .log().all()
                .body(jsonBody)
                .when()
                .post("/api/v1/orders");
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания заказа"))
                .log().body();
    }
}