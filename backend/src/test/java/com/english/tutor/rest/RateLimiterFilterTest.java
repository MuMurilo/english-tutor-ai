package com.english.tutor.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class RateLimiterFilterTest {

    @BeforeEach
    public void setUp() {
        RateLimiterFilter.resetTrackers();
    }

    @Test
    public void testRateLimiterBlocksRequests() {
        String uniqueEmail = "rate_limit_" + System.currentTimeMillis() + "@test.com";

        // Realiza 5 requisições de cadastro que falham na validação de senha (BadRequest), mas contam no limite
        for (int i = 0; i < 5; i++) {
            given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"123\", \"englishLevel\": \"BEGINNER\"}")
            .when()
                .post("/api/auth/register")
            .then()
                .statusCode(400);
        }

        // A 6ª requisição deve ser bloqueada com status 429 (Too Many Requests)
        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"123\", \"englishLevel\": \"BEGINNER\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(429);
    }
}
