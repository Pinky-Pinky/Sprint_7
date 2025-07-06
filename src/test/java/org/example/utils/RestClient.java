package org.example.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.with;

public class RestClient {
    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .setContentType("application/json")
                .build();
    }
}