package com.fundoonotes.controller;

import com.fundoonotes.dto.request.LoginRequestDto;
import com.fundoonotes.dto.request.UserRegisterRequestDto;
import com.fundoonotes.dto.response.LoginResponseDto;
import com.fundoonotes.dto.response.UserResponseDto;
import com.fundoonotes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(requestDto));
    }

    // POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }

    // GET /api/users/profile (requires token)
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile(
            @RequestHeader("Authorization") String token) {
        // Token parsing is handled in service layer via TokenUtil
        // For now returning a simple message — wire properly in Part 2 with Spring Security
        return ResponseEntity.ok().build();
    }
}
