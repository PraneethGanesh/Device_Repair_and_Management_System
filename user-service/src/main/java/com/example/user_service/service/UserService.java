package com.example.user_service.service;

import com.example.user_service.dto.AuthRequest;
import com.example.user_service.dto.AuthResponse;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse registerUser(UserDTO userDTO,String role){
       User user=new User();
       user.setName(userDTO.getName());
       user.setEmail(userDTO.getEmail());
       user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
       user.setRole(Role.valueOf(role));
       User savedUser=userRepository.save(user);
       String token= jwtUtil.generateToken(savedUser.getId(),savedUser.getEmail(), savedUser.getRole().name());
       return buildAuthResponse(savedUser,token);
    }

    public AuthResponse login(AuthRequest authRequest){
        User user=userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(()->new RuntimeException("User not found"));
        if(passwordEncoder.matches(authRequest.getPassword(),user.getPassword())){
            throw new BadCredentialsException("Incorrect password");
        }
        String token= jwtUtil.generateToken(user.getId(),user.getEmail(), user.getRole().name());
        return buildAuthResponse(user,token);
    }

    private AuthResponse buildAuthResponse(User user,String token){
        AuthResponse authResponse=new AuthResponse();
        authResponse.setId(user.getId());
        authResponse.setEmail(user.getEmail());
        authResponse.setName(user.getName());
        authResponse.setRole(user.getRole().name());
        authResponse.setToken(token);
        return authResponse;
    }

}
