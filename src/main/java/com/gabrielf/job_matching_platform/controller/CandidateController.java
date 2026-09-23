package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.request.CandidateRequest;
import com.gabrielf.job_matching_platform.dto.response.CandidateResponse;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.security.AuthenticatedUserProvider;
import com.gabrielf.job_matching_platform.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    public ResponseEntity<CandidateResponse> createProfile(
            @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CandidateRequest request) {
        User currentUser = authenticatedUserProvider.getCurrentUser(userDetails);
        CandidateResponse response = candidateService.createProfile(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> findById(@PathVariable  UUID id) {
        return ResponseEntity.ok(candidateService.findById(id));

    }
    @PutMapping("/{id}")
    public ResponseEntity<CandidateResponse> updateProfile (
            @PathVariable UUID id,  @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CandidateRequest request) {
        User currentUser = authenticatedUserProvider.getCurrentUser(userDetails);
        return ResponseEntity.ok(candidateService.updateProfile(id, currentUser.getId(), request));

    }
}
