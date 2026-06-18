package com.english.tutor.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class AuthResourceTest {

    @Test
    public void testAuthWorkflow() {
        String uniqueEmail = "user_" + System.currentTimeMillis() + "@test.com";

        // 1. Cadastrar usuário
        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"securePassword123\", \"englishLevel\": \"BEGINNER\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201);

        // 2. Fazer login com sucesso e receber token JWT
        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"securePassword123\"}")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());

        // 3. Fazer login com senha incorreta (deve falhar com 401)
        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"wrongpassword\"}")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401);
    }
}
