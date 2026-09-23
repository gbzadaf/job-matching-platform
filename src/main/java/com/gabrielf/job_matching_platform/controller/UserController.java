package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.request.RegisterRequest;
import com.gabrielf.job_matching_platform.dto.response.UserResponse;
import com.gabrielf.job_matching_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response =  userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById (@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));

    }
}
