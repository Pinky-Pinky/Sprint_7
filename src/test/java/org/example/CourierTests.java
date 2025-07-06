package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.CourierApi;
import org.example.models.CourierModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierTests {
    private static final ObjectMapper mapper = new ObjectMapper();
    private String courierId;
    private String login;

    @Before
    public void setUp() throws Exception {
        login = "test" + System.currentTimeMillis(); // Уникальный логин
        CourierModel courier = new CourierModel(login, "1234", null);
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all()
                .statusCode(201);
        courierId = response.jsonPath().getString("id"); // Проверяем, есть ли id
        System.out.println("Created courierId: " + courierId);
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            CourierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Test creating courier")
    @Description("Test creating courier with valid data")
    public void testCreateCourier() throws Exception {
        String testLogin = "test" + System.currentTimeMillis();
        CourierModel courier = new CourierModel(testLogin, "1234", null);
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Test creating courier without login")
    @Description("Test that creating courier without login fails")
    public void testCreateCourierWithoutLogin() throws Exception {
        CourierModel courier = new CourierModel("", "1234", null); // Без логина и firstName
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}