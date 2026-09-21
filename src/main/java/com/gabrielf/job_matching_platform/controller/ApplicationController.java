package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.request.ApplicationRequest;
import com.gabrielf.job_matching_platform.dto.response.ApplicationResponse;
import com.gabrielf.job_matching_platform.model.enums.ApplicationStatus;
import com.gabrielf.job_matching_platform.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{candidateId}")
    public ResponseEntity<ApplicationResponse> apply(
            @PathVariable UUID candidateId, @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.apply(candidateId, request);
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
