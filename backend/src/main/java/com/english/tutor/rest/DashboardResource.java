package com.english.tutor.rest;

import com.english.tutor.domain.Feedback;
import com.english.tutor.domain.FeedbackRepository;
import com.english.tutor.application.DashboardService;
import com.english.tutor.application.DidacticReportDto;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class DashboardResource {

    @Inject
    FeedbackRepository feedbackRepository;

    @Inject
    DashboardService dashboardService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/feedback")
    public List<Feedback> getFeedback() {
        Long userId = getUserIdFromJwt();
        return feedbackRepository.findByUserId(userId);
    }

    @GET
    @Path("/report")
    public DidacticReportDto getReport() {
        Long userId = getUserIdFromJwt();
        return dashboardService.generateReport(userId);
    }

    private Long getUserIdFromJwt() {
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
}
