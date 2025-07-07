package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.CourierApi;
import org.example.models.CourierModel;
import org.junit.Test;
import com.github.javafaker.Faker;
import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierTests {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Faker faker = new Faker();

    @Test
    @DisplayName("Test creating courier")
    @Description("Test creating courier with valid data")
    public void testCreateCourier() throws Exception {
        String testLogin = "test_" + faker.name().username();
        CourierModel courier = new CourierModel(testLogin, "1234", null);
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Test creating courier without password")
    @Description("Test that creating courier without password fails")
    public void testCreateCourierWithoutPassword() throws Exception {
        String testLogin = "test_" + faker.name().username();
        CourierModel courier = new CourierModel(testLogin, "", null);
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Test creating courier without login")
    @Description("Test that creating courier without login fails")
    public void testCreateCourierWithoutLogin() throws Exception {
        CourierModel courier = new CourierModel("", "1234", null);
        Response response = CourierApi.createCourier(courier);
        response.then()
                .log().all()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}