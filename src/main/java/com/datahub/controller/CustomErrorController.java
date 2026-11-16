package com.datahub.controller;

import com.datahub.dto.ApiResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fallback Controller for catching unmapped routes or unhandled servlet dispatch errors.
 * Ensures unknown paths always return a structured JSON 404 rather than default HTML error pages.
 */
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse<Void>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = HttpStatus.NOT_FOUND.value();
        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        String path = (requestUri != null) ? requestUri.toString() : "unknown";
        String message = (statusCode == 404)
                ? "Route not found: " + path
                : "Request failed with HTTP status " + statusCode;

        return ResponseEntity.status(statusCode)
                .body(ApiResponse.error(message));
    }
}

