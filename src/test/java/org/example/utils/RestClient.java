package org.example.utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestClient {
    private static class Config {
        public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    }

    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

    public static Response sendGetRequest(String endpoint, Map<String, Object> queryParams) {
        RequestSpecification request = given()
                .spec(getBaseSpec())
                .log().all();

        if (queryParams != null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                request.queryParam(entry.getKey(), entry.getValue());
            }
        }

        return request
                .when()
                .get(endpoint)
                .then()
                .extract().response();
    }

    public static Response sendDeleteRequest(String endpoint) {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .when()
                .delete(endpoint)
                .then()
                .extract().response();
    }

    public static Response sendPostRequest(String endpoint, String body, String authToken) {
        RequestSpecification request = given()
                .spec(getBaseSpec())
                .log().all()
                .body(body);

        if (authToken != null) {
            request.header("Authorization", "Bearer " + authToken);
        }

        return request
                .when()
                .post(endpoint)
                .then()
                .extract().response();
    }
}