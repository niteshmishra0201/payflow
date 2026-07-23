package com.niteshmishra.payflow.controller;

import com.niteshmishra.payflow.config.JwtService;
import com.niteshmishra.payflow.dto.LoginRequestDto;
import com.niteshmishra.payflow.dto.LoginResponseDto;
import com.niteshmishra.payflow.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AuthController {

    private final JwtService jwtService;

    @Value("${payflow.admin.username}")
    private String adminUsername;

    @Value("${payflow.admin.password}")
    private String adminPassword;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        if (!adminUsername.equals(request.getUsername()) ||
                !adminPassword.equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}