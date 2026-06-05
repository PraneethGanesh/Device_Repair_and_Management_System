package com.example.user_service.controller;

import com.example.user_service.dto.AuthRequest;
import com.example.user_service.dto.AuthResponse;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register/{role}")
    public AuthResponse registerUser(@PathVariable String role, @RequestBody UserDTO userDTO){
        return userService.registerUser(userDTO,role);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.login(authRequest));
    }

}
