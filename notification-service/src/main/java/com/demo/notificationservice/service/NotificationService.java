package com.demo.notificationservice.service;

import com.demo.notificationservice.entity.Notification;
import com.demo.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${news.api.url:https://hacker-news.firebaseio.com/v0/topstories.json}")
    private String newsApiUrl;

    public Notification sendNotification(Long userId, String message) {
        logger.info("Sending notification to user: {}", userId);
        
        Notification notification = new Notification(userId, message);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Simulate notification sending (email, SMS, push, etc.)
        logger.info("Notification sent successfully: {}", savedNotification.getId());
        
        return savedNotification;
    }

    public Optional<Notification> getNotificationById(Long id) {
        logger.info("Fetching notification by id: {}", id);
        return notificationRepository.findById(id);
    }

    public Map<String, Object> getLatestNews() {
        logger.info("Fetching latest news");
        
        try {
            // Get top story IDs from Hacker News API (free, no API key required)
            Integer[] topStoryIds = restTemplate.getForObject(newsApiUrl, Integer[].class);
            
            if (topStoryIds != null && topStoryIds.length > 0) {
                // Get details for the first 5 stories
                Map<String, Object> newsData = new java.util.HashMap<>();
                java.util.List<Map<String, Object>> articles = new java.util.ArrayList<>();
                
                for (int i = 0; i < Math.min(5, topStoryIds.length); i++) {
                    String storyUrl = "https://hacker-news.firebaseio.com/v0/item/" + topStoryIds[i] + ".json";
                    Map<String, Object> story = restTemplate.getForObject(storyUrl, Map.class);
                    if (story != null) {
                        articles.add(story);
                    }
                }
                
                newsData.put("articles", articles);
                newsData.put("totalResults", articles.size());
                newsData.put("status", "ok");
                
                logger.info("News data fetched successfully");
                return newsData;
            } else {
                throw new RuntimeException("No stories found");
            }
        } catch (Exception e) {
            logger.error("Failed to fetch news data", e);
            throw new RuntimeException("News service unavailable");
        }
    }
} 