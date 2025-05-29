package com.demo.orderservice.dto;

public class User {
    private Long id;
    private String name;
    private String email;
    private String city;
    
    // Constructors
    public User() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
} 