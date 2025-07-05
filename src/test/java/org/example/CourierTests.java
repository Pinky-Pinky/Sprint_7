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

public class CourierTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private String courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
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
    @DisplayName("Create courier successfully")
    @Description("Test that a courier can be created with valid data")
    public void testCreateCourier() {
        String login = "ninja" + System.currentTimeMillis();
        String body = "{\"login\": \"" + login + "\", \"password\": \"1234\", \"firstName\": \"saske\"}";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier");
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Login to get courier ID for cleanup
        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body("{\"login\": \"" + login + "\", \"password\": \"1234\"}")
                .post("/api/v1/courier/login");
        courierId = loginResponse.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Cannot create duplicate courier")
    @Description("Test that creating a courier with an existing login fails")
    public void testCannotCreateDuplicateCourier() {
        String login = "ninja" + System.currentTimeMillis();
        String body = "{\"login\": \"" + login + "\", \"password\": \"1234\", \"firstName\": \"saske\"}";
        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        // Login to get courier ID for cleanup
        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body("{\"login\": \"" + login + "\", \"password\": \"1234\"}")
                .post("/api/v1/courier/login");
        courierId = loginResponse.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Create courier missing field")
    @Description("Test that creating a courier without a required field fails")
    public void testCreateCourierMissingField() {
        String body = "{\"login\": \"ninja" + System.currentTimeMillis() + "\", \"firstName\": \"saske\"}";
        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}