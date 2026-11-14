package com.datahub.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Health check response payload providing system metrics and status.
 */
public class HealthResponse {

    private String status;
    private String uptime;
    private String timestamp;
    private int totalPosts;
    private long requestsServed;

    public HealthResponse() {
    }

    public HealthResponse(String status, String uptime, String timestamp, int totalPosts, long requestsServed) {
        this.status = status;
        this.uptime = uptime;
        this.timestamp = timestamp;
        this.totalPosts = totalPosts;
        this.requestsServed = requestsServed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getRequestsServed() {
        return requestsServed;
    }

    public void setRequestsServed(long requestsServed) {
        this.requestsServed = requestsServed;
    }
}
