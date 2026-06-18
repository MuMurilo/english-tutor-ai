package com.english.tutor.rest;

import com.english.tutor.application.ChatService;
import com.english.tutor.domain.ChatMessage;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class ChatResource {

    @Inject
    ChatService chatService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/history")
    public List<ChatMessage> getHistory() {
        Long userId = getUserIdFromJwt();
        return chatService.getChatHistory(userId);
    }

    @POST
    @Path("/send")
    public Response send(MessageRequest request) {
        if (request == null || request.content == null || request.content.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mensagem não pode ser vazia")
                    .build();
        }

        Long userId = getUserIdFromJwt();
        String englishLevel = jwt.getClaim("englishLevel");

        ChatMessage tutorResponse = chatService.sendMessage(userId, englishLevel, request.content);
        return Response.ok(tutorResponse).build();
    }

    private Long getUserIdFromJwt() {
        // SmallRye JWT claim values could be read as Numbers (Long/Integer/Double) depending on token construction
        Object userIdClaim = jwt.getClaim("userId");
        if (userIdClaim == null) {
            throw new WebApplicationException("Missing user ID claim in token", Response.Status.UNAUTHORIZED);
        }
        try {
            return Long.parseLong(userIdClaim.toString());
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Invalid user ID claim in token", Response.Status.UNAUTHORIZED);
        }
    }

    public static class MessageRequest {
        public String content;
    }
}
