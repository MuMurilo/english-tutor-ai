package com.english.tutor.rest;

import com.english.tutor.application.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.Optional;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        try {
            boolean success = authService.register(request.email, request.password, request.englishLevel);
            if (success) {
                return Response.status(Response.Status.CREATED).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", "E-mail já cadastrado no sistema"))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        Optional<String> tokenOpt = authService.login(request.email, request.password);
        if (tokenOpt.isPresent()) {
            return Response.ok(Map.of("token", tokenOpt.get())).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "E-mail ou senha incorretos"))
                    .build();
        }
    }

    public static class RegisterRequest {
        public String email;
        public String password;
        public String englishLevel;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
}
