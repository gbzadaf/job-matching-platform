package com.gabrielf.job_matching_platform.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApplicationRequest(

        @NotNull(message = "Job id is required")
        UUID jobId
) {
}
