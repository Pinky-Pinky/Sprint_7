package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private final String color;

    public OrderTests(String color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"[\"BLACK\"]"},
                {"[\"GREY\"]"},
                {"[\"BLACK\", \"GREY\"]"},
                {"[]"}
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Create order with different colors")
    @Description("Test that an order can be created with different color options")
    public void testCreateOrder() {
        String body = "{\"firstName\": \"Naruto\", \"lastName\": \"Uchiha\", \"address\": \"Konoha, 142 apt.\", \"metroStation\": \"4\", \"phone\": \"+78003553535\", \"rentTime\": 5, \"deliveryDate\": \"2020-06-06\", \"comment\": \"Saske, come back to Konoha\", \"color\": " + color + "}";
        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}