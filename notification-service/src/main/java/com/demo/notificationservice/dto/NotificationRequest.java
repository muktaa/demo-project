package com.demo.notificationservice.dto;

import javax.validation.constraints.NotNull;

public class NotificationRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private String message;
    
    // Constructors
    public NotificationRequest() {}
    
    public NotificationRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 