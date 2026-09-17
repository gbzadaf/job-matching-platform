package com.gabrielf.job_matching_platform.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CandidateRequest(

        @Size(max = 1000, message = "Bio must be at most 1000 characters")
        String bio,

        @NotEmpty(message = "At least one skill is required")
        List<String> skills
) {
}
