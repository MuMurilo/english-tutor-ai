package com.english.tutor.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Provider
public class RateLimiterFilter implements ContainerRequestFilter {
    private static final ConcurrentHashMap<String, RequestTracker> trackers = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5; // max 5 requests per minute
    private static final long TIME_WINDOW_MS = 60000; // 1 minute

    static void resetTrackers() {
        trackers.clear();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.endsWith("api/auth/login") || path.endsWith("api/auth/register")) {
            String ip = requestContext.getHeaderString("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = "unknown-ip";
            }

            long now = System.currentTimeMillis();
            RequestTracker tracker = trackers.compute(ip, (key, current) -> {
                if (current == null || (now - current.windowStart) > TIME_WINDOW_MS) {
                    return new RequestTracker(now, 1);
                } else {
                    current.count.incrementAndGet();
                    return current;
                }
            });

            if (tracker.count.get() > MAX_REQUESTS) {
                requestContext.abortWith(Response.status(429)
                        .entity(Map.of("message", "Muitas tentativas de login ou cadastro. Tente novamente em 1 minuto."))
                        .build());
            }
        }
    }

    private static class RequestTracker {
        final long windowStart;
        final AtomicInteger count;

        RequestTracker(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(count);
        }
    }
}
