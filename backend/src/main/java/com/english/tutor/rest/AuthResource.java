package com.english.tutor.rest;

import com.english.tutor.application.AuthService;
import org.jboss.logging.Logger;
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

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        try {
            boolean success = authService.register(request.email, request.password, request.englishLevel);
            if (success) {
                LOG.info("Usuário cadastrado com sucesso: " + request.email);
                return Response.status(Response.Status.CREATED).build();
            } else {
                LOG.warn("Falha no cadastro: E-mail já cadastrado - " + request.email);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", "E-mail já cadastrado no sistema"))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Falha de validação no cadastro de " + request.email + ": " + e.getMessage());
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
            LOG.info("Login bem-sucedido para o usuário: " + request.email);
            return Response.ok(Map.of("token", tokenOpt.get())).build();
        } else {
            LOG.warn("Falha de login para o usuário: " + request.email + " - E-mail ou senha incorretos");
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
