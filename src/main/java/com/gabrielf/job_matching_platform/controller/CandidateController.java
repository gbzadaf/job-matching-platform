package com.gabrielf.job_matching_platform.controller;

import com.gabrielf.job_matching_platform.dto.request.CandidateRequest;
import com.gabrielf.job_matching_platform.dto.response.CandidateResponse;
import com.gabrielf.job_matching_platform.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    // userId temporario, depois com o Security trocamos isso pela extração do usuário autenticado via JWT,o controller
    // vai pegar do contexto de segurança em vez de confiar em quem está chamando.
    @PostMapping("/{userId}")
    public ResponseEntity<CandidateResponse> createProfile(
            @PathVariable UUID userId, @Valid @RequestBody CandidateRequest request) {
        CandidateResponse response = candidateService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> findById(@PathVariable  UUID id) {
        return ResponseEntity.ok(candidateService.findById(id));

    }
    @PutMapping("/{id}")
    public ResponseEntity<CandidateResponse> updateProfile (
            @PathVariable UUID id, @Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.ok(candidateService.updateProfile(id, request));

    }
}
