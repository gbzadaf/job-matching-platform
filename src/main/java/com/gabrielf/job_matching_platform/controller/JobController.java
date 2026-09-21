package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.request.JobRequest;
import com.gabrielf.job_matching_platform.dto.response.JobResponse;
import com.gabrielf.job_matching_platform.model.enums.JobStatus;
import com.gabrielf.job_matching_platform.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/{recruiterId}")
    public ResponseEntity<JobResponse> create(
            @PathVariable UUID recruiterId, @Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.create(recruiterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> findById (@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.findById(id));

    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> findOpenJobs(Pageable pageable) {
        return ResponseEntity.ok(jobService.findOpenJobs(pageable));

    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<Page<JobResponse>> findRecruiter (@PathVariable UUID recruiterId, Pageable pageable) {
        return ResponseEntity.ok(jobService.findByRecruiter(recruiterId, pageable));

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobResponse> updateStatus(
            @PathVariable UUID id, @RequestParam JobStatus status) {
        return ResponseEntity.ok(jobService.updateStatus(id, status));

    }
}
