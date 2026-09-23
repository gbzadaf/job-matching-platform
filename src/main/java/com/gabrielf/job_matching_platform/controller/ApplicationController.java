package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.request.ApplicationRequest;
import com.gabrielf.job_matching_platform.dto.response.ApplicationResponse;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.model.enums.ApplicationStatus;
import com.gabrielf.job_matching_platform.security.AuthenticatedUserProvider;
import com.gabrielf.job_matching_platform.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(
            @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ApplicationRequest request) {
        User currentUser = authenticatedUserProvider.getCurrentUser(userDetails);
        ApplicationResponse response = applicationService.apply(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<Page<ApplicationResponse>> findByCandidate(
            @PathVariable UUID candidateId,
            Pageable pageable) {
        return ResponseEntity.ok(applicationService.findByCandidate(candidateId, pageable));

    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<Page<ApplicationResponse>> findByJob(
            @PathVariable UUID jobId,
            Pageable pageable) {
        return ResponseEntity.ok(applicationService.findByJob(jobId, pageable));

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));

    }
}
