package com.datahub.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Middleware Filter executing on every HTTP request.
 * Logs method, path, and timestamp to the console with ANSI colors:
 * GET: Green, POST: Blue, PUT: Yellow, DELETE: Red.
 * Output format: [GET] /posts - 10:05 AM
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    // ANSI escape codes for terminal coloring
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";   // GET
    private static final String ANSI_BLUE = "\u001B[34m";    // POST
    private static final String ANSI_YELLOW = "\u001B[33m";  // PUT
    private static final String ANSI_RED = "\u001B[31m";     // DELETE
    private static final String ANSI_CYAN = "\u001B[36m";    // OTHER
    private static final String ANSI_BOLD = "\u001B[1m";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    private final AtomicLong requestCounter = new AtomicLong(0);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long currentCount = requestCounter.incrementAndGet();

        String method = request.getMethod().toUpperCase();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = (queryString != null && !queryString.isEmpty()) ? uri + "?" + queryString : uri;

        String formattedTime = LocalTime.now().format(TIME_FORMATTER);
        String color = getColorForMethod(method);

        // Format: [GET] /posts - 10:05 AM
        String logMessage = String.format("%s[%s]%s %s - %s",
                color, method, ANSI_RESET, fullPath, formattedTime);

        System.out.println(logMessage);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Optional completion hook
        }
    }

    private String getColorForMethod(String method) {
        return switch (method) {
            case "GET" -> ANSI_GREEN;
            case "POST" -> ANSI_BLUE;
            case "PUT" -> ANSI_YELLOW;
            case "DELETE" -> ANSI_RED;
            default -> ANSI_CYAN;
        };
    }

    public long getRequestCount() {
        return requestCounter.get();
    }
}

