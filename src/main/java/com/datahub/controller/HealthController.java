package com.datahub.controller;

import com.datahub.dto.ApiResponse;
import com.datahub.dto.HealthResponse;
import com.datahub.middleware.RequestLoggingFilter;
import com.datahub.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Health check controller providing operational telemetry and system uptime.
 */
@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    private final Instant startTime = Instant.now();
    private final PostService postService;
    private final RequestLoggingFilter loggingFilter;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    public HealthController(PostService postService, RequestLoggingFilter loggingFilter) {
        this.postService = postService;
        this.loggingFilter = loggingFilter;
    }

    /**
     * GET /health
     * Returns server status, formatted uptime, ISO timestamp, total post count, and request counter.
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<HealthResponse>> getHealth() {
        Duration uptimeDuration = Duration.between(startTime, Instant.now());
        long hours = uptimeDuration.toHours();
        long minutes = uptimeDuration.toMinutesPart();
        long seconds = uptimeDuration.toSecondsPart();
        String formattedUptime = String.format("%02dh %02dm %02ds", hours, minutes, seconds);

        String currentTimestamp = ISO_FORMATTER.format(Instant.now());

        HealthResponse health = new HealthResponse(
                "UP",
                formattedUptime,
                currentTimestamp,
                postService.count(),
                loggingFilter.getRequestCount()
        );

        return ResponseEntity.ok(ApiResponse.success("Server is healthy and operational", health));
    }
}

