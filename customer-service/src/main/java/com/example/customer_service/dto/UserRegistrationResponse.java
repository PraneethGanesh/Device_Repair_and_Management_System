package com.example.customer_service.dto;

public class UserRegistrationResponse {

    private String token;
    private String id;
    private String name;
    private String email;
    private String role;

    public String getToken() { return token; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setToken(String token) { this.token = token; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}
