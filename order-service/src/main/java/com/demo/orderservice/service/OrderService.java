package com.demo.orderservice.service;

import com.demo.orderservice.dto.User;
import com.demo.orderservice.entity.Order;
import com.demo.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public Order createOrder(Order order) {
        logger.info("Creating order for user: {}", order.getUserId());
        
        // Validate user exists by calling user service
        validateUser(order.getUserId());
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with id: {}", savedOrder.getId());
        
        // Send notification
        sendNotification(savedOrder);
        
        return savedOrder;
    }

    public Optional<Order> getOrderById(Long id) {
        logger.info("Fetching order by id: {}", id);
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        logger.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    private void validateUser(Long userId) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            User user = restTemplate.getForObject(url, User.class);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            logger.info("User validated: {}", user.getName());
        } catch (Exception e) {
            logger.error("Failed to validate user: {}", userId, e);
            throw new RuntimeException("User validation failed");
        }
    }

    private void sendNotification(Order order) {
        try {
            String url = notificationServiceUrl + "/api/notifications/send";
            String message = String.format("Order %d created for user %d", 
                                         order.getId(), order.getUserId());
            
            NotificationRequest request = new NotificationRequest(order.getUserId(), message);
            restTemplate.postForObject(url, request, String.class);
            logger.info("Notification sent for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Failed to send notification for order: {}", order.getId(), e);
            // Don't fail the order creation if notification fails
        }
    }

    public static class NotificationRequest {
        private Long userId;
        private String message;

        public NotificationRequest(Long userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 