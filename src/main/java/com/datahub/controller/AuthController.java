package com.datahub.controller;

import com.datahub.dto.ApiResponse;
import com.datahub.dto.LoginRequest;
import com.datahub.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

/**
 * Controller providing mock authentication.
 * Generates realistic Base64 URL-encoded JWT tokens without external heavy auth libraries.
 */
@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    private static final String MOCK_SECRET = "the-data-hub-in-memory-super-secret-key-2026";

    /**
     * POST /login
     * Validates email and password presence. Returns a mock JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody(required = false) LoginRequest request) {
        if (request == null ||
                request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Missing required credentials: both 'email' and 'password' are required"));
        }

        String token = generateMockJwt(request.getEmail().trim());
        LoginResponse response = new LoginResponse(token);

        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    /**
     * Generates a realistic 3-part header.payload.signature JWT using Base64URL encoding.
     */
    private String generateMockJwt(String email) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        // 1. JWT Header
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String encodedHeader = encoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

        // 2. JWT Payload
        long now = Instant.now().getEpochSecond();
        long exp = now + 86400; // 24 hours expiry
        String payloadJson = String.format(
                "{\"sub\":\"%s\",\"email\":\"%s\",\"iss\":\"The Data Hub\",\"iat\":%d,\"exp\":%d,\"role\":\"DEVELOPER\"}",
                escapeJson(email), escapeJson(email), now, exp
        );
        String encodedPayload = encoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        // 3. Realistic Mock Signature
        String signingInput = encodedHeader + "." + encodedPayload;
        String encodedSignature;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((signingInput + MOCK_SECRET).getBytes(StandardCharsets.UTF_8));
            encodedSignature = encoder.encodeToString(hash);
        } catch (Exception e) {
            encodedSignature = encoder.encodeToString("mock-valid-signature-sha256".getBytes(StandardCharsets.UTF_8));
        }

        return encodedHeader + "." + encodedPayload + "." + encodedSignature;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }
}
