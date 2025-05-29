package com.demo.userservice.service;

import com.demo.userservice.entity.User;
import com.demo.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${weather.api.url:https://api.open-meteo.com/v1/forecast}")
    private String weatherApiUrl;

    public User createUser(User user) {
        logger.info("Creating user: {}", user.getName());
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user by id: {}", id);
        return userRepository.findById(id);
    }

    public Map<String, Object> getUserWeather(Long userId) {
        logger.info("Fetching weather for user: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        String city = user.getCity() != null ? user.getCity() : "London";
        
        // Using Open-Meteo API (free, no API key required)
        // Get coordinates for the city first (simplified - using default coordinates)
        double latitude = getLatitudeForCity(city);
        double longitude = getLongitudeForCity(city);
        
        String url = String.format("%s?latitude=%.2f&longitude=%.2f&current_weather=true&timezone=auto", 
                                 weatherApiUrl, latitude, longitude);
        
        try {
            Map<String, Object> weatherData = restTemplate.getForObject(url, Map.class);
            logger.info("Weather data fetched successfully for city: {}", city);
            return weatherData;
        } catch (Exception e) {
            logger.error("Failed to fetch weather data for city: {}", city, e);
            throw new RuntimeException("Weather service unavailable");
        }
    }
    
    private double getLatitudeForCity(String city) {
        // Simplified mapping - in real app, you'd use a geocoding service
        switch (city.toLowerCase()) {
            case "london": return 51.5074;
            case "new york": return 40.7128;
            case "paris": return 48.8566;
            case "tokyo": return 35.6762;
            case "mumbai": return 19.0760;
            default: return 51.5074; // Default to London
        }
    }
    
    private double getLongitudeForCity(String city) {
        // Simplified mapping - in real app, you'd use a geocoding service
        switch (city.toLowerCase()) {
            case "london": return -0.1278;
            case "new york": return -74.0060;
            case "paris": return 2.3522;
            case "tokyo": return 139.6503;
            case "mumbai": return 72.8777;
            default: return -0.1278; // Default to London
        }
    }
} 