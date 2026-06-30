package com.english.tutor.infrastructure;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "gemini-api")
public interface GeminiClient {

    @POST
    @Path("/v1beta/models/{model}:generateContent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    GeminiResponse generateContent(
        @PathParam("model") String model,
        @HeaderParam("x-goog-api-key") String apiKey,
        GeminiRequest request
    );
}
