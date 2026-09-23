package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.response.JobMatchResponse;
import com.gabrielf.job_matching_platform.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/matching")
@RequiredArgsConstructor
public class MatchController {

    private final MatchingService  matchingService;

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<JobMatchResponse>> findMatchesForCandidate(@PathVariable UUID candidateId) {
        return ResponseEntity.ok(matchingService.findMatchesForCandidate(candidateId));

    }

    @GetMapping("/candidate/{candidateId}/job/{jobId}")
    public ResponseEntity<JobMatchResponse> calculateMatchForJob(@PathVariable UUID candidateId,
                                                                 @PathVariable UUID jobId) {
        return ResponseEntity.ok(matchingService.calculateMatchForJob(candidateId, jobId));

    }
}
