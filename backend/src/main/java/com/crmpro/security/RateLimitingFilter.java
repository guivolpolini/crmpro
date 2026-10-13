package com.crmpro.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory sliding window rate limiter to protect sensitive authentication endpoints
 * against brute-force attacks and credential stuffing.
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private final Map<String, ClientRequestWindow> ipRequestCounts = new ConcurrentHashMap<>();

    private static class ClientRequestWindow {
        int count;
        long windowStartMs;

        ClientRequestWindow(long windowStartMs) {
            this.count = 1;
            this.windowStartMs = windowStartMs;
        }
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Aplicar rate limit em endpoints de autenticação
        if (path.startsWith("/api/v1/auth/login") || path.startsWith("/api/v1/auth/register")) {
            String clientIp = extractClientIp(request);
            long now = Instant.now().toEpochMilli();

            ClientRequestWindow window = ipRequestCounts.compute(clientIp, (ip, current) -> {
                if (current == null || (now - current.windowStartMs) > 60_000) {
                    return new ClientRequestWindow(now);
                }
                current.count++;
                return current;
            });

            if (window.count > MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit atingido para o IP {} na rota {}", clientIp, path);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                    {
                        "success": false,
                        "message": "Muitas tentativas em pouco tempo. Por segurança, aguarde 1 minuto.",
                        "data": null,
                        "timestamp": "%s"
                    }
                """.formatted(Instant.now()));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }
}
