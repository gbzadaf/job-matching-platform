package com.gabrielf.job_matching_platform.dto.response;

import com.gabrielf.job_matching_platform.model.Candidate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CandidateResponse(
        UUID id,
        UserResponse user,
        String bio,
        List<SkillResponse> skills,
        LocalDateTime createdAt
) {
    public static CandidateResponse fromEntity (Candidate candidate) {
        return new CandidateResponse(
                candidate.getId(),
                UserResponse.fromEntity(candidate.getUser()),
                candidate.getBio(),
                candidate.getSkills().stream()
                        .map(SkillResponse::fromEntity)
                        .toList(),
                candidate.getCreatedAt()
        );
    }
}
