package com.english.tutor.rest;

import com.english.tutor.application.AuthService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class DashboardResourceTest {

    @Inject
    AuthService authService;

    @Test
    public void shouldReturn401WhenNotAuthenticated() {
        given()
        .when()
            .get("/api/dashboard/feedback")
        .then()
            .statusCode(401);
    }

    @Test
    public void shouldReturnFeedbacksWhenAuthenticated() {
        // 1. Cadastrar e logar um usuário único para obter token JWT válido
        String uniqueEmail = "dash_" + System.currentTimeMillis() + "@test.com";
        authService.register(uniqueEmail, "securePassword123", "BEGINNER");
        
        Optional<String> tokenOpt = authService.login(uniqueEmail, "securePassword123");
        assertTrue(tokenOpt.isPresent());
        String token = tokenOpt.get();

        // 2. Chamar o endpoint autenticado
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/dashboard/feedback")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }
}
