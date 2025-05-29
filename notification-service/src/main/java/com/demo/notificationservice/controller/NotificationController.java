package com.demo.notificationservice.controller;

import com.demo.notificationservice.dto.NotificationRequest;
import com.demo.notificationservice.entity.Notification;
import com.demo.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.sendNotification(
            request.getUserId(), 
            request.getMessage()
        );
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/news")
    public ResponseEntity<Map<String, Object>> getLatestNews() {
        try {
            Map<String, Object> news = notificationService.getLatestNews();
            return ResponseEntity.ok(news);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 